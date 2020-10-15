package com.example.httpserver.app.ui.view.folders.folder;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
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

    public static final String TAG = ContextDialog.class.getName();

    private EditText text;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.context);
        builder.setPositiveButton("Apply", (dialog, which) -> {
            NavController controller = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            Bundle bundle = new Bundle();
            bundle.putString("context", text.getText().toString());
            Log.i(TAG, "Apply context change: " + text.getText());
            controller.navigate(R.id.nav_folder, bundle, new NavOptions.Builder().setPopUpTo(R.id.nav_folders, true).build());
            Log.i(TAG, "Navigate to folder");
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            Log.i(TAG, "Cancel context change: " + text.getText());
            NavController controller = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            controller.navigate(R.id.nav_folder, new Bundle(), new NavOptions.Builder().setPopUpTo(R.id.nav_folders, true).build());
            Log.i(TAG, "Navigate to folder");
        });
        View view = null;
        builder.setView( view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_text, null));
        text = view.findViewById(R.id.edit_text);
        text.setHint(R.string.context);

        Bundle bundle = getArguments();
        if(bundle != null && bundle.containsKey("context")) {
            Log.i(TAG, "Receive context name: " + bundle.getString("context"));
            text.setText(bundle.getString("context"));
        }
        return builder.create();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }


}
