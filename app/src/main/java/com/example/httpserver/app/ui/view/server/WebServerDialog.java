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
import com.example.httpserver.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.dialog.MaterialDialogs;

public class WebServerDialog extends DialogFragment {

    private EditText httpPort;
    private EditText ftpPort;
    private CheckBox enableFtp;
    private CheckBox enableHttp;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setPositiveButton("Apply", (dialog, which) -> {
            onSave();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {

        });
        View view = getLayoutInflater().inflate(R.layout.dialog_server, null);
        builder.setView(view);
        httpPort = view.findViewById(R.id.http_port);
        ftpPort = view.findViewById(R.id.ftp_port);
        enableFtp = view.findViewById(R.id.enable_ftp);
        enableHttp = view.findViewById(R.id.enable_http);
        return builder.create();
    }

    private void onSave() {
        
    }
}
