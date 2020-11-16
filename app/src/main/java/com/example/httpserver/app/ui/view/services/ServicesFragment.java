package com.example.httpserver.app.ui.view.services;

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
import com.example.httpserver.R;
import com.example.httpserver.app.ui.adapter.NetworkAdapter;
import com.example.httpserver.app.ui.adapter.ServiceAdapter;

public class ServicesFragment extends Fragment {

    private ServicesViewModel model;
    private RecyclerView networkContainer;
    private RecyclerView serviceContainer;
    private SwipeRefreshLayout refresh;

    private NetworkAdapter networkAdapter;
    private ServiceAdapter serviceAdapter;

    public static ServicesFragment newInstance() {
        return new ServicesFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.services_container, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        networkContainer = view.findViewById(R.id.interface_container);
        serviceContainer = view.findViewById(R.id.service_container);

        networkContainer.setAdapter((networkAdapter = new NetworkAdapter()));
        serviceContainer.setAdapter((serviceAdapter = new ServiceAdapter()));
        (refresh = view.findViewById(R.id.swipe_refresh))
                .setOnRefreshListener(this::refresh);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        model = new ViewModelProvider(this).get(ServicesViewModel.class);
        model.network().observe(getViewLifecycleOwner(), networks -> {
            networkAdapter.setNetworks(networks);
            networkAdapter.notifyDataSetChanged();
        });
        model.service().observe(getViewLifecycleOwner(), services -> {
            serviceAdapter.setServices(services);
            serviceAdapter.notifyDataSetChanged();
        });
    }

    private void refresh() {
        refresh.setRefreshing(true);
        model.refresh();
        refresh.setRefreshing(false);
    }

}