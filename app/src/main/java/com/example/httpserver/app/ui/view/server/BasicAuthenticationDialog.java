package com.example.httpserver.app.ui.view.server;

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

public class BasicAuthenticationDialog extends DialogFragment {

    private EditText username;
    private EditText password;
    private Button ok;
    private Button cancel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_password, container);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        username = view.findViewById(R.id.username);
        password = view.findViewById(R.id.password);
        ok = view.findViewById(R.id.ok);
        cancel = view.findViewById(R.id.cancel);

        Bundle args = getArguments();
        if(args != null) {
            username.setText(args.getString("username"));
            password.setText(args.getString("password"));
        }

        ok.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("username", username.getText().toString());
            bundle.putString("password", password.getText().toString());
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.nav_server, bundle);
        });

        cancel.setOnClickListener(v -> {
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.nav_server);
        });
    }

}
