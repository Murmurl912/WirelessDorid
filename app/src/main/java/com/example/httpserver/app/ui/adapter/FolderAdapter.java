package com.example.httpserver.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.httpserver.R;
import com.example.httpserver.app.repository.entity.Folder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> {

    private final BiConsumer<View, FolderViewHolder> DEFAULT_CONSUMER = (v, h) -> {};
    private BiConsumer<View, FolderViewHolder> consumer = DEFAULT_CONSUMER;
    private List<Folder> folders;

    public FolderAdapter() {
        folders = new ArrayList<>();
    }

    public void folders(List<Folder> folders) {
        this.folders = folders == null ? this.folders : folders;
    }

    public void handler(BiConsumer<View, FolderViewHolder> handler) {
        this.consumer = handler == null ? DEFAULT_CONSUMER : handler;;
    }

    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FolderViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_folder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull  FolderAdapter.FolderViewHolder holder, int position) {
        holder.bind(consumer, folders.get(position));
    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

    public static class FolderViewHolder extends RecyclerView.ViewHolder {
        public Folder folder;

        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(BiConsumer<View, FolderViewHolder> consumer, Folder folder) {
            this.folder = folder;
            itemView.setOnClickListener(v -> {
                consumer.accept(v, FolderViewHolder.this);
            });
            ((TextView)itemView.findViewById(R.id.folder_path)).setText(folder.path);
            ((TextView)itemView.findViewById(R.id.path_title)).setText(folder.name);
        }
    }

}
