package com.example.mediqueue.ui;

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
import com.example.mediqueue.databinding.FragmentLoginBinding;
import com.example.mediqueue.model.User;
import com.example.mediqueue.repository.MediRepository;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private MediRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        repository = new MediRepository(requireActivity().getApplication());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.tvBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString();
            String password = binding.etPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            repository.login(email, password, user -> {
                if (getActivity() == null) return;
                
                if (user != null) {
                    saveUser(user);
                    getActivity().runOnUiThread(() -> {
                        if ("admin".equals(user.role)) {
                            Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_adminDashboard);
                        } else {
                            Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_patientDashboard);
                        }
                    });
                } else {
                    getActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Invalid credentials", Toast.LENGTH_SHORT).show());
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
