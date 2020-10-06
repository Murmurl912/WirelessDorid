package com.example.httpserver.app.ui.view.server;

import android.widget.*;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.httpserver.R;
import com.example.httpserver.app.ui.NavigationFragment;
import com.google.android.material.switchmaterial.SwitchMaterial;

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
    private EditText password;
    private TextView totpPassword;
    private Spinner totpKey;
    private Spinner tlsCert;

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
        model.config().thenAccept(config -> {
           config.port.observe(getViewLifecycleOwner(), p -> {
               port.setText(p);
           });
            config.address.observe(getViewLifecycleOwner(), a -> {

            });
            config.basic.observe(getViewLifecycleOwner(), b -> {
                basic.setChecked(b);
            });
            config.totp.observe(getViewLifecycleOwner(), t -> {
                totp.setChecked(t);
            });
            config.tls.observe(getViewLifecycleOwner(), t -> {
                tls.setChecked(t);
            });

            config.username.observe(getViewLifecycleOwner(), u -> {
                username.setText(u);
            });
            config.password.observe(getViewLifecycleOwner(), p -> {
                password.setText(p);
            });

        });
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
        totpKey = view.findViewById(R.id.totp_key);
        tlsCert = view.findViewById(R.id.tls_cert);

        setup();
    }

    private void setup() {

    }
}