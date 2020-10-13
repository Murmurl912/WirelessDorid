package com.example.httpserver.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.httpserver.R;
import com.example.httpserver.app.App;
import com.example.httpserver.app.services.HttpService;
import com.google.android.material.switchmaterial.SwitchMaterial;

public abstract class NavigationFragment extends Fragment {


    public NavigationFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main_menu, menu);
        SwitchMaterial server = (menu.findItem(R.id.server_switch).getActionView().findViewById(R.id.server_switch_button));
        if(server != null) {
            App.app().serverStatus().observe(getViewLifecycleOwner(), status -> {
                switch (status) {
                    case "running":
                        server.setChecked(true);
                        break;
                    case "stopped":
                    default:
                        server.setChecked(false);
                }
            });

            server.setOnClickListener(v -> {
                boolean checked = server.isChecked();
                if(checked) {
                    requireActivity().stopService(new Intent(requireContext(), HttpService.class));
                } else {
                    requireActivity().startService(new Intent(requireContext(), HttpService.class));
                }
            });
        }
    }


}