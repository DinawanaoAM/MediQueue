package com.example.mediqueue.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.mediqueue.R;
import com.example.mediqueue.databinding.FragmentAdminDashboardBinding;
import com.example.mediqueue.model.Appointment;
import com.example.mediqueue.repository.MediRepository;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdminDashboardFragment extends Fragment {

    private FragmentAdminDashboardBinding binding;
    private MediRepository repository;

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if (result.getContents() == null) {
                    Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_LONG).show();
                } else {
                    handleScanResult(result.getContents());
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminDashboardBinding.inflate(inflater, container, false);
        repository = new MediRepository(requireActivity().getApplication());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String today = new SimpleDateFormat("yyyy-M-d", Locale.getDefault()).format(new Date());
        repository.getAppointmentsForDate(today).observe(getViewLifecycleOwner(), appointments -> {
            if (appointments != null) {
                binding.tvTotalAppointments.setText(String.format(Locale.getDefault(), "%02d", appointments.size()));
                int checkedInCount = 0;
                for (Appointment a : appointments) {
                    if ("Completed".equals(a.status)) {
                        checkedInCount++;
                    }
                }
                binding.tvCheckedIn.setText(String.format(Locale.getDefault(), "%02d", checkedInCount));
            }
        });

        binding.tvLogout.setOnClickListener(v -> logout());

        binding.btnScan.setOnClickListener(v -> {
            ScanOptions options = new ScanOptions();
            options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
            options.setPrompt("Scan Patient Appointment QR");
            options.setCameraId(0);
            options.setBeepEnabled(true);
            options.setBarcodeImageEnabled(true);
            barcodeLauncher.launch(options);
        });

        binding.btnManageAnnouncements.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_adminDashboard_to_announcements));

        binding.btnUserManagement.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_adminDashboard_to_userManagement));
    }

    private void logout() {
        SharedPreferences pref = requireActivity().getSharedPreferences("MediQueue", Context.MODE_PRIVATE);
        pref.edit().clear().apply();
        Navigation.findNavController(requireView()).navigate(R.id.landingFragment);
    }

    private void handleScanResult(String qrData) {
        repository.getAppointmentByQr(qrData, appointment -> {
            if (appointment != null) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> showAppointmentDialog(appointment));
                }
            } else {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> 
                        Toast.makeText(getContext(), "Invalid QR Code", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void showAppointmentDialog(Appointment appointment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Patient Check-in");
        builder.setMessage("Patient: " + appointment.patientName + "\nService: " + appointment.service + "\nStatus: " + appointment.status);
        
        builder.setPositiveButton("Mark as Completed", (dialog, which) -> {
            appointment.status = "Completed";
            repository.updateAppointment(appointment);
        });
        
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
