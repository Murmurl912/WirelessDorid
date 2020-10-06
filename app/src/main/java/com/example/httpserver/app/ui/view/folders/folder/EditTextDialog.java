package com.example.httpserver.app.ui.view.folders.folder;

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
import androidx.navigation.Navigation;
import com.example.httpserver.R;

public class EditTextDialog extends DialogFragment {

    protected Bundle bundle = new Bundle();
    protected EditText text;
    protected TextView title;
    private Button ok;
    private Button cancel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = new Bundle();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_edit_text, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        assert args != null;

        title = view.findViewById(R.id.title);
        title.setText(args.getString("title", ""));
        bundle.putString("title", title.getText().toString());

        text = view.findViewById(R.id.edit_text);
        text.setText(args.getString("text", ""));

        cancel = view.findViewById(R.id.cancel);
        cancel.setOnClickListener(v -> {
            onCancel();
            back();
        });

        ok = view.findViewById(R.id.ok);
        ok.setOnClickListener(v -> {
            onOk();
            back();
        });
    }

    protected void onOk() {
        bundle.putString("text", text.getText().toString());
        EditTextDialog.this.setArguments(bundle);
    }

    protected void onCancel() {

    }

    private void back() {
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.nav_folder, bundle);
    }
}
