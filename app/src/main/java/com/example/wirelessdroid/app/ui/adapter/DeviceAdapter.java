package com.example.wirelessdroid.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.wirelessdroid.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    private final List<Map<String, String>> devices;

    public DeviceAdapter() {
        devices = new ArrayList<>();
    }

    public void setDevices(List<Map<String, String>> devices) {
        this.devices.clear();
        this.devices.addAll(devices);
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DeviceViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_device, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        holder.bind(devices.get(position));
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public static class DeviceViewHolder extends RecyclerView.ViewHolder {
        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(Map<String, String> device) {
            TextView name = itemView.findViewById(R.id.device_name);
            TextView description = itemView.findViewById(R.id.device_description);

            name.setText(device.get("name"));
            description.setText(device.get("description"));
        }
    }
}
