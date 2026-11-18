package com.example.mediqueue.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mediqueue.databinding.FragmentManageAnnouncementsBinding;
import com.example.mediqueue.model.Announcement;
import com.example.mediqueue.repository.MediRepository;

public class ManageAnnouncementsFragment extends Fragment {

    private FragmentManageAnnouncementsBinding binding;
    private MediRepository repository;
    private AnnouncementAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentManageAnnouncementsBinding.inflate(inflater, container, false);
        repository = new MediRepository(requireActivity().getApplication());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.tvBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        adapter = new AnnouncementAdapter();
        binding.rvAnnouncements.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvAnnouncements.setAdapter(adapter);

        repository.getAllAnnouncements().observe(getViewLifecycleOwner(), announcements -> {
            if (announcements != null) {
                adapter.setAnnouncements(announcements);
            }
        });

        binding.btnAdd.setOnClickListener(v -> {
            String title = binding.etTitle.getText().toString();
            String content = binding.etContent.getText().toString();

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Announcement announcement = new Announcement(title, content, System.currentTimeMillis());
            repository.insertAnnouncement(announcement);
            
            binding.etTitle.setText("");
            binding.etContent.setText("");
            Toast.makeText(getContext(), "Announcement added", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
