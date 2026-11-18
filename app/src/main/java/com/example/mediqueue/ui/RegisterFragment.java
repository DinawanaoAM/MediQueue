package com.example.mediqueue.ui;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.mediqueue.R;
import com.example.mediqueue.databinding.FragmentRegisterBinding;
import com.example.mediqueue.model.User;
import com.example.mediqueue.repository.MediRepository;

import java.util.Calendar;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private MediRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        repository = new MediRepository(requireActivity().getApplication());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.tvBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        binding.etBirthdate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(requireContext(), (view1, year, month, dayOfMonth) -> {
                String date = year + "-" + (month + 1) + "-" + dayOfMonth;
                binding.etBirthdate.setText(date);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        binding.btnRegister.setOnClickListener(v -> {
            String name = binding.etName.getText().toString();
            String birthdate = binding.etBirthdate.getText().toString();
            String address = binding.etAddress.getText().toString();
            String phone = binding.etPhone.getText().toString();
            String email = binding.etEmail.getText().toString();
            String password = binding.etPassword.getText().toString();
            String confirmPassword = binding.etConfirmPassword.getText().toString();

            if (name.isEmpty() || birthdate.isEmpty() || address.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = new User(name, email, password, "patient");
            user.birthdate = birthdate;
            user.address = address;
            user.phoneNumber = phone;

            repository.insertUser(user, id -> {
                if (getActivity() == null) return;
                if (id > 0) {
                    user.id = id.intValue();
                    saveUser(user);
                    getActivity().runOnUiThread(() -> 
                        Navigation.findNavController(v).navigate(R.id.action_registerFragment_to_patientDashboard));
                } else {
                    getActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Registration failed", Toast.LENGTH_SHORT).show());
                }
            });
        });
    }

    private void saveUser(User user) {
        SharedPreferences pref = requireActivity().getSharedPreferences("MediQueue", Context.MODE_PRIVATE);
        pref.edit().putInt("userId", user.id).putString("userRole", user.role).apply();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
