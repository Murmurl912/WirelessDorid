package com.example.wirelessdroid.app.ui.view.devices;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.wirelessdroid.R;
import com.example.wirelessdroid.app.ui.adapter.DeviceAdapter;

public class DevicesFragment extends Fragment {

    private DevicesViewModel model;
    private DeviceAdapter adapter;
    private RecyclerView container;
    private SwipeRefreshLayout refresh;

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
        (refresh = view.findViewById(R.id.swipe_refresh))
                .setOnRefreshListener(this::refresh);
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
        refresh.setRefreshing(true);
        model.refresh();
        refresh.setRefreshing(false);
    }

}