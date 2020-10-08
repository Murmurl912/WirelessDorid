package com.example.httpserver.app.ui.view.server;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.httpserver.R;
import com.example.httpserver.app.App;
import com.example.httpserver.app.LiveServerConfig;
import com.example.httpserver.app.ui.NavigationFragment;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

public class ServerFragment extends NavigationFragment {

    private ServerViewModel model;
    private TextView port;
    private Spinner addresses;
    private TextView status;
    private Button action;
    private SwitchMaterial basic;
    private SwitchMaterial totp;
    private SwitchMaterial tls;
    private TextView username;
    private TextView password;
    private TextView totpPassword;
    private ArrayAdapter<String> adapter;
    private TextView usernameLabel;
    private TextView passwordLabel;

    private final BiConsumer<String, Object> configListener = (key, value) -> {
        switch (key) {
            case "port":
                port.setText(value.toString());
                break;
            case "address":
                model.address().observe(getViewLifecycleOwner(), networks -> {
                    int index = networks.indexOf(value.toString());
                    adapter.clear();
                    adapter.addAll(networks);
                    if(index > 0) {
                        addresses.setSelection(index);
                    } else {
                        addresses.setSelection(0);
                    }
                    adapter.notifyDataSetChanged();
                });
                break;
            case "status":
                if(value.toString().equals("running")) {
                    status.setTextColor(Color.parseColor("#00ff00"));
                    action.setText("Stop Server");

                    passwordLabel.setEnabled(false);
                    usernameLabel.setEnabled(false);
                    addresses.setEnabled(false);
                    port.setEnabled(false);
                    totp.setEnabled(false);
                    basic.setEnabled(false);
                    tls.setEnabled(false);
                    username.setEnabled(false);
                    password.setEnabled(false);
                    totpPassword.setEnabled(false);
                } else {
                    status.setTextColor(Color.parseColor("#ff0000"));
                    action.setText("Start Server");

                    passwordLabel.setEnabled(true);
                    usernameLabel.setEnabled(true);
                    addresses.setEnabled(true);
                    port.setEnabled(true);
                    totp.setEnabled(true);
                    basic.setEnabled(true);
                    tls.setEnabled(true);
                    username.setEnabled(true);
                    password.setEnabled(true);
                    totpPassword.setEnabled(true);
                }
                status.setText(value.toString());
                break;
            case "basic":
                basic.setChecked(Boolean.parseBoolean(value.toString()));
                break;
            case "totp":
                totp.setChecked(Boolean.parseBoolean(value.toString()));
                break;
            case "tls":
                tls.setChecked(Boolean.parseBoolean(value.toString()));
                break;
            case "username":
                username.setText(value.toString());
                break;
            case "password":
                password.setText(value.toString());
                break;
        }
    };

    public static ServerFragment newInstance() {
        return new ServerFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_server, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        model = new ViewModelProvider(this).get(ServerViewModel.class);
        model.config().observe(getViewLifecycleOwner(), configListener);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        port = view.findViewById(R.id.port);
        addresses = view.findViewById(R.id.address);
        status = view.findViewById(R.id.status);
        action = view.findViewById(R.id.server_action);

        basic = view.findViewById(R.id.basic);
        totp = view.findViewById(R.id.totp);
        tls = view.findViewById(R.id.tls);

        username = view.findViewById(R.id.username);
        password = view.findViewById(R.id.password);
        totpPassword = view.findViewById(R.id.totp_password);

        passwordLabel = view.findViewById(R.id.password_label);
        usernameLabel = view.findViewById(R.id.username_label);

        setup();
    }

    private void setup() {
        basic.setOnClickListener(v -> {
            model.config().set("basic", basic.isChecked());
        });
        totp.setOnClickListener(v -> {
            model.config().set("totp", totp.isChecked());
        });
        tls.setOnClickListener(v -> {
            model.config().set("tls", tls.isChecked());
        });

        action.setOnClickListener(v -> {
            LiveServerConfig config = App.app().config();
            if("running".equals(config.status().getValue())) {
                App.app().stop();
                config.set("status", "stopped");
            } else {
                App.app().start();
                config.set("status", "running");
            }
        });

        username.setOnClickListener(this::basic);

        password.setOnClickListener(this::basic);

        port.setOnClickListener(v -> {

        });

        addresses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                model.config().set("address", adapter.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                addresses.setSelection(0);
            }
        });

        adapter = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addresses.setAdapter(adapter);

    }

    private void basic(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("username", username.getText().toString());
        bundle.putString("password", password.getText().toString());
        Navigation.findNavController(view).navigate(R.id.nav_basic_auth, bundle);
    }


}