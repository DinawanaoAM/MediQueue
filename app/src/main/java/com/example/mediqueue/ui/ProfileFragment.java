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

import com.example.mediqueue.R;
import com.example.mediqueue.databinding.FragmentProfileBinding;
import com.example.mediqueue.repository.MediRepository;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private MediRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        repository = new MediRepository(requireActivity().getApplication());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences pref = requireActivity().getSharedPreferences("MediQueue", Context.MODE_PRIVATE);
        int userId = pref.getInt("userId", -1);

        if (userId != -1) {
            repository.getUserById(userId).observe(getViewLifecycleOwner(), user -> {
                if (user != null) {
                    binding.tvName.setText(user.name);
                    binding.tvEmail.setText(user.email);
                    binding.tvPhone.setText(user.phoneNumber);
                    binding.tvBirthdate.setText(user.birthdate);
                    binding.tvAddress.setText(user.address);
                }
            });
        }

        binding.btnLogout.setOnClickListener(v -> {
            pref.edit().clear().apply();
            Navigation.findNavController(v).navigate(R.id.landingFragment);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
