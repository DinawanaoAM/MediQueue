package com.example.mediqueue.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mediqueue.R;
import com.example.mediqueue.databinding.FragmentPatientDashboardBinding;
import com.example.mediqueue.model.Appointment;
import com.example.mediqueue.repository.MediRepository;

public class PatientDashboardFragment extends Fragment {

    private FragmentPatientDashboardBinding binding;
    private MediRepository repository;
    private Appointment activeAppointment;
    private AnnouncementAdapter announcementAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPatientDashboardBinding.inflate(inflater, container, false);
        repository = new MediRepository(requireActivity().getApplication());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences pref = requireActivity().getSharedPreferences("MediQueue", Context.MODE_PRIVATE);
        int userId = pref.getInt("userId", -1);

        // Setup Recycler
        announcementAdapter = new AnnouncementAdapter();
        binding.rvAnnouncements.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvAnnouncements.setAdapter(announcementAdapter);

        if (userId != -1) {
            repository.getLatestPendingAppointment(userId).observe(getViewLifecycleOwner(), appointment -> {
                if (appointment != null) {
                    activeAppointment = appointment;
                    binding.tvBookingStatus.setText(appointment.service);
                    binding.tvApptDetails.setVisibility(View.VISIBLE);
                    binding.tvApptDetails.setText(getString(R.string.appointment_info, appointment.date, appointment.time));
                } else {
                    activeAppointment = null;
                    binding.tvBookingStatus.setText(R.string.no_booking_yet);
                    binding.tvApptDetails.setVisibility(View.GONE);
                }
            });

            repository.getAllAnnouncements().observe(getViewLifecycleOwner(), announcements -> {
                if (announcements != null) {
                    announcementAdapter.setAnnouncements(announcements);
                }
            });
        }

        binding.cardUpcoming.setOnClickListener(v -> {
            if (activeAppointment != null) {
                Bundle bundle = new Bundle();
                bundle.putString("qrData", activeAppointment.qrCodeData);
                Navigation.findNavController(v).navigate(R.id.action_patientDashboard_to_qrConfirmation, bundle);
            }
        });

        binding.btnBookNew.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_patientDashboard_to_bookAppointment));

        // Note: Bottom navigation usually handled in MainActivity, but for simplicity we use buttons or direct navigations.
        // User module mentioned Bottom/Top nav bar. Adding simple navigation placeholders.
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
