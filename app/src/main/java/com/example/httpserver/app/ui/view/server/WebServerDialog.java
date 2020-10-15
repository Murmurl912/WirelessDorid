package com.example.httpserver.app.ui.view.server;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
import com.example.httpserver.app.repository.entity.ServerConfig;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.dialog.MaterialDialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class WebServerDialog extends DialogFragment {

    private EditText httpPort;
    private EditText ftpPort;
    private CheckBox enableFtp;
    private CheckBox enableHttp;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setPositiveButton(requireContext().getText(R.string.apply), (dialog, which) -> {
            onSave();
        });
        builder.setNegativeButton(requireContext().getText(R.string.cancel), (dialog, which) -> {
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    .navigate(R.id.nav_server, null, new NavOptions.Builder().setPopUpTo(R.id.nav_server, true).build());
        });
        View view = getLayoutInflater().inflate(R.layout.dialog_server, null);
        builder.setView(view);
        httpPort = view.findViewById(R.id.http_port);
        ftpPort = view.findViewById(R.id.ftp_port);
        enableFtp = view.findViewById(R.id.enable_ftp);
        enableHttp = view.findViewById(R.id.enable_http);
        Bundle bundle = getArguments();
        if(bundle != null && bundle.containsKey("server_config")) {
            ServerConfig config = bundle.getParcelable("server_config");
            if(config == null) {
                config = new ServerConfig();
            }
            httpPort.setText(config.http_port + "");
            ftpPort.setText(config.ftp_port + "");
            enableHttp.setChecked(config.http);
            enableFtp.setChecked(config.ftp);
        }
        enableFtp.setOnClickListener(v -> {
            ftpPort.setEnabled(enableFtp.isChecked());
        });
        enableHttp.setOnClickListener(v -> {
            httpPort.setEnabled(enableHttp.isChecked());
        });
        return builder.create();
    }

    private void onSave() {
        if(!range(ftpPort.getText().toString())) {
            error(requireContext().getString(R.string.ftp_port_illegal) + ftpPort);
            return;
        }

        if(!range(httpPort.getText().toString())) {
            error(requireContext().getString(R.string.http_port_illegal) + httpPort);
            return;
        }

        if(Objects.equals(ftpPort.getText().toString(), httpPort.getText().toString())) {
            error(requireContext().getString(R.string.ftp_http_port_conflict) + ftpPort.getText());
            return;
        }

        List<Configuration> configurations = new ArrayList<>();
        Configuration ftpPortConfig = new Configuration("ftp_port", ftpPort.getText().toString());
        Configuration ftpConfig = new Configuration("ftp", Boolean.toString(enableFtp.isChecked()));
        Configuration httpPortConfig = new Configuration("ftp_port", httpPort.getText().toString());
        Configuration httpConfig = new Configuration("ftp", Boolean.toString(enableHttp.isChecked()));
        configurations.add(ftpConfig);
        configurations.add(ftpPortConfig);
        configurations.add(httpConfig);
        configurations.add(httpPortConfig);

        App.app().executor().submit(()-> App.app().db().configuration().save(configurations));

        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                .navigate(R.id.nav_server, null, new NavOptions.Builder().setPopUpTo(R.id.nav_server, true).build());
    }

    private static boolean range(String port) {
        try {
            int p = Integer.parseInt(port);
            return  p > 2000 && p < 65536;
        } catch (Exception e) {
            return false;
        }
    }

    private void error(String message) {
        AlertDialog dialog = new AlertDialog.Builder(getContext()).setMessage(message).create();
        dialog.setTitle(R.string.error);
        dialog.setCancelable(true);
        dialog.setButton(BUTTON_POSITIVE, requireContext().getString(R.string.ok), (dialog1, which) -> dialog1.dismiss());
        dialog.show();
    }

}
