package com.example.httpserver.app.ui.view.folders.folder;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.example.httpserver.R;

public class ContextDialog extends DialogFragment {

    private EditText text;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Set Name");
        builder.setPositiveButton("Apply", (dialog, which) -> {
            NavController controller = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            Bundle bundle = new Bundle();
            bundle.putString("context", text.getText().toString());
            controller.navigate(R.id.nav_folder, bundle, new NavOptions.Builder().setPopUpTo(R.id.nav_folders, true).build());
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {

        });
        View view = null;
        builder.setView( view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_text, null));
        text = view.findViewById(R.id.edit_text);
        text.setHint("Context Name");
        return builder.create();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }


}
