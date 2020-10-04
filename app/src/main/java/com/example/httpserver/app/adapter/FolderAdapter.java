package com.example.httpserver.app.adapter;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.httpserver.R;

import java.util.function.BiConsumer;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> {

    private final BiConsumer<View, FolderViewHolder> DEFAULT_CONSUMER = (v, h) -> {};
    private BiConsumer<View, FolderViewHolder> consumer = DEFAULT_CONSUMER;

    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FolderViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_folder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull  FolderAdapter.FolderViewHolder holder, int position) {
        holder.bind(consumer, Environment.getExternalStorageDirectory().getAbsolutePath());
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public static class FolderViewHolder extends RecyclerView.ViewHolder {
        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(BiConsumer<View, FolderViewHolder> consumer, String title) {
            itemView.setOnClickListener(v -> {
                consumer.accept(v, FolderViewHolder.this);
            });
            ((TextView)itemView.findViewById(R.id.folder_path)).setText(title);
        }
    }

}
