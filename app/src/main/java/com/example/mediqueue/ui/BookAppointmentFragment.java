package com.example.mediqueue.ui;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.mediqueue.R;
import com.example.mediqueue.databinding.FragmentBookAppointmentBinding;
import com.example.mediqueue.model.Appointment;
import com.example.mediqueue.repository.MediRepository;

import java.util.Calendar;
import java.util.UUID;

public class BookAppointmentFragment extends Fragment {

    private FragmentBookAppointmentBinding binding;
    private MediRepository repository;
    private String selectedDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBookAppointmentBinding.inflate(inflater, container, false);
        repository = new MediRepository(requireActivity().getApplication());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.tvBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        // Setup Service Spinner
        String[] services = {
                getString(R.string.general_checkup),
                getString(R.string.prenatal),
                getString(R.string.immunization),
                getString(R.string.dental)
        };
        ArrayAdapter<String> serviceAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, services);
        binding.spinnerService.setAdapter(serviceAdapter);

        // Setup Time Spinner
        String[] times = {"09:00 AM", "09:30 AM", "10:00 AM", "10:30 AM", "11:00 AM", "02:00 PM", "02:30 PM", "03:00 PM"};
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, times);
        binding.spinnerTime.setAdapter(timeAdapter);

        binding.tvSelectedDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(requireContext(), (view1, year, month, dayOfMonth) -> {
                selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                binding.tvSelectedDate.setText(selectedDate);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        binding.btnConfirm.setOnClickListener(v -> {
            if (selectedDate == null) {
                Toast.makeText(getContext(), "Please select a date", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences pref = requireActivity().getSharedPreferences("MediQueue", Context.MODE_PRIVATE);
            int userId = pref.getInt("userId", -1);
            String service = binding.spinnerService.getSelectedItem().toString();
            String time = binding.spinnerTime.getSelectedItem().toString();
            String qrData = UUID.randomUUID().toString();

            repository.getUserById(userId).observe(getViewLifecycleOwner(), user -> {
                if (user != null) {
                    Appointment appt = new Appointment(userId, user.name, service, selectedDate, time, "Pending", qrData);
                    repository.bookAppointment(appt, result -> {
                        if (getActivity() == null) return;
                        getActivity().runOnUiThread(() -> {
                            if ("Success".equals(result)) {
                                Bundle bundle = new Bundle();
                                bundle.putString("qrData", qrData);
                                Navigation.findNavController(v).navigate(R.id.action_bookAppointment_to_qrConfirmation, bundle);
                            } else {
                                Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
                            }
                        });
                    });
                }
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
