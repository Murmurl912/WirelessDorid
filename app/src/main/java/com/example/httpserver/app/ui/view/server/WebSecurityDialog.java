package com.example.httpserver.app.ui.view.server;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import com.example.httpserver.R;
import com.example.httpserver.app.App;
import com.example.httpserver.app.repository.entity.Configuration;

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
        builder.setTitle(R.string.authentication);
        builder.setPositiveButton(requireContext().getText(R.string.apply), (dialog, which) -> {
            onSave();
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    .navigate(R.id.nav_server, null, new NavOptions.Builder().setPopUpTo(R.id.nav_server, true).build());
        });

        builder.setNegativeButton(requireContext().getText(R.string.cancel), (dialog, which) -> {
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    .navigate(R.id.nav_server, null, new NavOptions.Builder().setPopUpTo(R.id.nav_server, true).build());
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
