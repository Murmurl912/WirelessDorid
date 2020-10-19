package com.example.httpserver.app.ui.view.server;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import com.example.httpserver.R;

public class QRCodeDialog extends DialogFragment {

    public static final String TAG = QRCodeDialog.class.getName();

    private View root;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        builder.setPositiveButton(R.string.ok, (dialog, which) -> {

        });

        Bundle bundle = getArguments();
        if(bundle != null) {
            String text = bundle.getString("text");
            Log.i(TAG, "Receive qr text: " + text);
            builder.setMessage("Link: " + text);
            LoaderManager manager = LoaderManager.getInstance(this);
            manager.initLoader(0, bundle, new LoaderManager.LoaderCallbacks<Bitmap>() {
                @NonNull
                @Override
                public Loader<Bitmap> onCreateLoader(int id, @Nullable Bundle args) {
                    return load(text);
                }

                @Override
                public void onLoadFinished(@NonNull Loader<Bitmap> loader, Bitmap data) {
                    ((ImageView)root).setImageBitmap(data);
                }

                @Override
                public void onLoaderReset(@NonNull Loader<Bitmap> loader) {

                }
            }).forceLoad();
        }
        root = getLayoutInflater().inflate(R.layout.dialog_qr_code, null);
        builder.setView(root);
        return builder.create();
    }

    private AsyncTaskLoader<Bitmap> load(String text) {
        return new AsyncTaskLoader<Bitmap>(requireContext()) {
            @Nullable
            @Override
            public Bitmap loadInBackground() {
                return encode(text);
            }

            @Override
            public void deliverResult(@Nullable Bitmap data) {
                ((ImageView)root).setImageBitmap(data);
            }
        };
    }

    private Bitmap encode(String text) {
        QRGEncoder qrgEncoder = new QRGEncoder(text, QRGContents.Type.TEXT, 400);
        return qrgEncoder.getBitmap();
    }

}
