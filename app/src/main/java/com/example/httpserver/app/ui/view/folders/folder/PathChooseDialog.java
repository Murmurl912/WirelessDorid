package com.example.httpserver.app.ui.view.folders.folder;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.httpserver.R;


public class PathChooseDialog extends EditTextDialog {

    public static final String TAG = PathChooseDialog.class.getName();


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View select = view.findViewById(R.id.select);
        select.setOnClickListener(v -> {
            startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE), 0);
        });
        select.setVisibility(View.VISIBLE);
        text = view.findViewById(R.id.edit_text);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            text.setText(uri.toString());
        }
    }
}
