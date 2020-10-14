package com.example.httpserver.app.ui.view.server;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.example.httpserver.R;
import com.example.httpserver.app.App;
import com.example.httpserver.app.repository.entity.Configuration;
import com.example.httpserver.app.repository.entity.ServerConfig;

import java.util.ArrayList;

public class WebSecurityDialog extends DialogFragment {

    private EditText username;
    private EditText password;
    private CheckBox tls;
    private CheckBox totp;
    private CheckBox basic;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Authentication");
        builder.setPositiveButton("Apply", (dialog, which) -> {
            onSave();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {

        });

        View view = getLayoutInflater().inflate(R.layout.dialog_security, null);
        username = view.findViewById(R.id.username);
        password = view.findViewById(R.id.password);
        tls = view.findViewById(R.id.tls);
        totp = view.findViewById(R.id.totp);
        basic = view.findViewById(R.id.basic);
        builder.setView(view);
        Bundle bundle = getArguments();
        if(bundle != null && bundle.containsKey("server_config")) {
            ServerConfig config = bundle.getParcelable("server_config");
            if(config == null) {
                config = new ServerConfig();
            }
            username.setText(config.username);
            password.setText(config.password);
            tls.setChecked(config.tls);
            totp.setChecked(config.totp);
            basic.setChecked(config.basic);


        }
        username.setEnabled(basic.isChecked());
        password.setEnabled(basic.isChecked());
        basic.setOnClickListener(v -> {
            username.setEnabled(basic.isChecked());
            password.setEnabled(basic.isChecked());
        });
        return builder.create();
    }

    private void onSave() {
        ArrayList<Configuration> configurations = new ArrayList<>();
        Configuration tlsConfig = new Configuration("tls", Boolean.toString(tls.isChecked()));
        Configuration basicConfig = new Configuration("basic", Boolean.toString(tls.isChecked()));
        Configuration totpConfig = new Configuration("totp", Boolean.toString(tls.isChecked()));
        Configuration userConfig = new Configuration("username", username.getText().toString());
        Configuration passConfig = new Configuration("password", password.getText().toString());
        configurations.add(userConfig);
        configurations.add(passConfig);
        configurations.add(tlsConfig);
        configurations.add(basicConfig);
        configurations.add(totpConfig);
        App.app().executor().submit(()-> App.app().db().configuration().save(configurations));
    }
}
