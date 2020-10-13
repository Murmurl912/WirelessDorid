package com.example.httpserver.app.ui.view.server;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.Navigation;

import com.example.httpserver.R;

public class WebSecurityDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Authentication");
        builder.setPositiveButton("Apply", (dialog, which) -> {

        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {

        });
        builder.setView(R.layout.dialog_security);
        return builder.create();
    }

}
