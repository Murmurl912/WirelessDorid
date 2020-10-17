package com.example.httpserver.app.ui.view.folders.folder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.DialogFragment;
import com.example.httpserver.R;
import com.example.httpserver.common.util.FileUtils;


public class PathChooseDialog extends DialogFragment {

    public static final String TAG = PathChooseDialog.class.getName();
    private EditText text;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Path");
        builder.setCancelable(false);
        builder.setNeutralButton("Select", (dialog, which) -> {
            PathChooseDialog.this.startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE), 0);
        });

        builder.setPositiveButton("Apply", (dialog, which) -> {

        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {

        });
        View view = null;
        builder.setView( view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_text, null));
        text = view.findViewById(R.id.edit_text);
        text.setHint("Folder Path");
        return builder.create();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            assert uri != null;
            text.setText(uri.toString());
            String path = FileUtils.getAbsolutePathFromTreeUri(requireContext(), uri);
        }
    }
}
