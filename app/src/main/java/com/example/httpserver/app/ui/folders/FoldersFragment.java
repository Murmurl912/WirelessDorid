package com.example.httpserver.app.ui.folders;

import android.content.Intent;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.httpserver.R;
import com.example.httpserver.app.adapter.FolderAdapter;
import com.example.httpserver.app.ui.folders.folder.FolderSettingActivity;

import java.util.function.BiConsumer;

public class FoldersFragment extends Fragment {

    private FoldersViewModel mViewModel;
    private FolderAdapter adapter;

    public static FoldersFragment newInstance() {
        return new FoldersFragment();
    }

    private final BiConsumer<View, FolderAdapter.FolderViewHolder> handler = (view, holder) -> {
        NavController controller = Navigation.findNavController(view);
        Bundle bundle = new Bundle();
        bundle.putString("path", holder.folder.path);
        controller.navigate(R.id.nav_folder, bundle);
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_folders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.folder_container);
        adapter = new FolderAdapter();
        adapter.handler(handler);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
        view.findViewById(R.id.add_folder).setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.nav_folder);
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FoldersViewModel.class);
        mViewModel.folders().observe(getViewLifecycleOwner(), folders -> {
            adapter.folders(folders);
            adapter.notifyDataSetChanged();
        });
    }

}