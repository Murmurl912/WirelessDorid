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

public class NetworkAdapter extends RecyclerView.Adapter<NetworkAdapter.NetworkInterfaceViewHolder> {

    private final List<Map<String, String>> networks;

    public NetworkAdapter() {
        networks = new ArrayList<>();
    }

    public void setNetworks(List<Map<String, String>> networks) {
        this.networks.clear();
        this.networks.addAll(networks);
    }

    @NonNull
    @Override
    public NetworkInterfaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_network, parent, false);
        return new NetworkInterfaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NetworkInterfaceViewHolder holder, int position) {
        holder.bind(networks.get(position));
    }

    @Override
    public int getItemCount() {
        return networks.size();
    }

    public static class NetworkInterfaceViewHolder extends RecyclerView.ViewHolder {
        public NetworkInterfaceViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(Map<String, String> network) {
            TextView name = itemView.findViewById(R.id.interface_name);
            TextView address = itemView.findViewById(R.id.interface_address);

            name.setText(network.get("name"));
            address.setText(network.get("address"));
        }
    }

}
