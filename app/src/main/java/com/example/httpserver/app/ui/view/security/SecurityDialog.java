package com.example.httpserver.app.ui.view.security;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.httpserver.R;

public class SecurityDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireContext())
                .setView(LayoutInflater.from(requireContext()).inflate(R.layout.dialog_security, null))
                .setTitle("Security")
                .create();
    }

}
