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

import com.example.mediqueue.databinding.FragmentHistoryBinding;
import com.example.mediqueue.repository.MediRepository;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;
    private MediRepository repository;
    private HistoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        repository = new MediRepository(requireActivity().getApplication());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.tvBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        adapter = new HistoryAdapter();
        binding.rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvHistory.setAdapter(adapter);

        SharedPreferences pref = requireActivity().getSharedPreferences("MediQueue", Context.MODE_PRIVATE);
        int userId = pref.getInt("userId", -1);

        if (userId != -1) {
            repository.getAppointmentsForUser(userId).observe(getViewLifecycleOwner(), appointments -> {
                if (appointments != null) {
                    adapter.setAppointments(appointments);
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
