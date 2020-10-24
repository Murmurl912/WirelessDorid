package com.example.httpserver.app.ui.view.services;

import android.view.*;
import android.widget.TextView;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.example.httpserver.R;
import com.example.httpserver.app.ui.adapter.AddressAdapter;
import com.example.httpserver.app.ui.adapter.ServiceAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ServicesFragment extends Fragment {

    private ServicesViewModel model;
    private AddressAdapter addressAdapter;
    private ServiceAdapter serviceAdapter;

    private RecyclerView serviceContainer;
    private RecyclerView addressContainer;

    private TextView serviceUnavailable;
    private TextView networkUnavailable;

    public static ServicesFragment newInstance() {
        return new ServicesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_services, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addressContainer = view.findViewById(R.id.address_container);
        addressContainer.setAdapter((addressAdapter = new AddressAdapter()));

        serviceContainer = view.findViewById(R.id.service_container);
        serviceContainer.setAdapter((serviceAdapter = new ServiceAdapter()));

        serviceUnavailable = view.findViewById(R.id.services_unavailable);
        networkUnavailable = view.findViewById(R.id.network_unavailable);

        FloatingActionButton action = view.findViewById(R.id.main_action);

        action.setTag("off");
        action.setOnClickListener(v -> {
            String tag = action.getTag().toString();
            if(tag.equals("on")) {
                action.setImageResource(R.drawable.ic_start_dark);
                v.setTag("off");
            } else {
                action.setImageResource(R.drawable.ic_stop_dark);
                v.setTag("on");
            }
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        model = new ViewModelProvider(this).get(ServicesViewModel.class);
        model.addresses().observe(getViewLifecycleOwner(), list -> {
            if(list.isEmpty()) {
                addressContainer.setVisibility(View.GONE);
                networkUnavailable.setVisibility(View.VISIBLE);
                return;
            }
            addressContainer.setVisibility(View.VISIBLE);
            networkUnavailable.setVisibility(View.GONE);
            addressAdapter.setAddresses(list);
            addressAdapter.notifyDataSetChanged();
        });
        model.services().observe(getViewLifecycleOwner(), list -> {
            if(list.isEmpty()) {
                serviceContainer.setVisibility(View.GONE);
                serviceUnavailable.setVisibility(View.VISIBLE);
                return;
            }
            serviceContainer.setVisibility(View.VISIBLE);
            serviceUnavailable.setVisibility(View.GONE);
            serviceAdapter.setServices(list);
           serviceAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {

    }
}