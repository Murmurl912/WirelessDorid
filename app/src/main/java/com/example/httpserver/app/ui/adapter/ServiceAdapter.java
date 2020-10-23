package com.example.httpserver.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.httpserver.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceHolder> {

    private List<Map<String, String>> services;

    public ServiceAdapter() {
        services = new ArrayList<>();
    }

    public void setServices(List<Map<String, String>> services) {
        this.services.clear();
        this.services.addAll(services);
    }

    @NonNull
    @Override
    public ServiceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_services, parent, false);
        return new ServiceHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceHolder holder, int position) {
        holder.bind();
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public class ServiceHolder extends RecyclerView.ViewHolder {
        public ServiceHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind() {
            Map<String, String> map = services.get(getAdapterPosition());
            TextView title = itemView.findViewById(R.id.service_title);
            TextView description = itemView.findViewById(R.id.service_description);
            title.setText(map.get("title"));
            description.setText(map.get("description"));
        }
    }
}
