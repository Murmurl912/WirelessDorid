package com.example.httpserver.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.httpserver.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NetworkAddressAdapter extends RecyclerView.Adapter<NetworkAddressAdapter.NetworkViewHolder> {

    public static class NetworkViewHolder extends RecyclerView.ViewHolder {
        public NetworkViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(String string) {
            TextView textView = itemView.findViewById(R.id.address);
            textView.setText(string);
        }
    }

    private final List<String> addresses;

    public NetworkAddressAdapter() {
        addresses = new ArrayList<>();
    }

    public void setAddresses(List<String> addresses) {
        this.addresses.clear();
        this.addresses.addAll(addresses);
    }

    @NonNull
    @Override
    public NetworkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_network_address, parent, false);
        return new NetworkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NetworkViewHolder holder, int position) {
        holder.bind(addresses.get(position));
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }
}
