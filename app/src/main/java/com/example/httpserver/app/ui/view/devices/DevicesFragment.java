package com.example.httpserver.app.ui.view.devices;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.example.httpserver.R;
import com.example.httpserver.app.ui.adapter.DeviceAdapter;

public class DevicesFragment extends Fragment {

    private DevicesViewModel model;
    private DeviceAdapter adapter;
    private RecyclerView container;

    public static DevicesFragment newInstance() {
        return new DevicesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.devices_container, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        container = view.findViewById(R.id.device_container);
        adapter = new DeviceAdapter();
        container.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        model = new ViewModelProvider(this).get(DevicesViewModel.class);
        model.device().observe(getViewLifecycleOwner(), devices -> {
            adapter.setDevices(devices);
            adapter.notifyDataSetChanged();
        });
    }

    private void refresh() {
        model.refresh();
    }
}