package com.example.httpserver.app.ui.view.folders;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.httpserver.R;
import com.example.httpserver.app.ui.NavigationFragment;
import com.example.httpserver.app.ui.adapter.FolderAdapter;
import com.example.httpserver.app.ui.view.folders.folder.FolderFragment;

import java.util.function.BiConsumer;

public class FoldersFragment extends NavigationFragment {

    public static final String TAG = FolderFragment.class.getName();

    private FoldersViewModel mViewModel;
    private FolderAdapter adapter;

    public static FoldersFragment newInstance() {
        return new FoldersFragment();
    }

    private final BiConsumer<View, FolderAdapter.FolderViewHolder> handler = (view, holder) -> {
        NavController controller = Navigation.findNavController(view);
        Bundle bundle = new Bundle();
        bundle.putParcelable("folder", holder.folder);
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
            Navigation.findNavController(v).navigate(R.id.nav_folder, new Bundle());
            Log.i(TAG, "Navigate to create folder");
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FoldersViewModel.class);
        mViewModel.folders().observe(getViewLifecycleOwner(), folders -> {
            adapter.folders(folders);
            adapter.notifyDataSetChanged();
            Log.i(TAG, "Load folder records successfully, entry size: " + folders.size());
        });
    }

}