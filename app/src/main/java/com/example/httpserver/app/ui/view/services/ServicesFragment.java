package com.example.httpserver.app.ui.view.services;

import android.view.*;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.example.httpserver.R;
import com.example.httpserver.app.ui.adapter.NetworkAdapter;
import com.example.httpserver.app.ui.adapter.ServiceAdapter;

public class ServicesFragment extends Fragment {

    private ServicesViewModel model;
    private RecyclerView networkContainer;
    private RecyclerView serviceContainer;

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
        model.refresh();
    }

}