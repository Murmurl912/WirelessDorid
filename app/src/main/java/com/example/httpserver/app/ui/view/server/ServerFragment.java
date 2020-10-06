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

        setup();
    }

    private void setup() {

    }
}