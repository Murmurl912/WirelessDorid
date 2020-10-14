package com.example.httpserver.app.ui.view.server;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.httpserver.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.dialog.MaterialDialogs;

public class WebServerDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setPositiveButton("Apply", (dialog, which) -> {

        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {

        });
        View view = getLayoutInflater().inflate(R.layout.dialog_server, null);
        builder.setView(view);
        return builder.create();
    }
}
