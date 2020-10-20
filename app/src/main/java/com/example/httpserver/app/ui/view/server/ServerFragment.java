package com.example.httpserver.app.ui.view.server;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.example.httpserver.R;
import com.example.httpserver.app.App;
import com.example.httpserver.app.repository.entity.Configuration;
import com.example.httpserver.app.ui.NavigationFragment;

public class ServerFragment extends NavigationFragment {
    public static final String TAG = ServerFragment.class.getName();

    private ServerViewModel model;
    private TextView port;
    private Spinner addresses;
    private ImageButton statusIcon;
    private TextView status;
    private Button action;
    private TextView username;
    private TextView password;
    private TextView pin;
    private ArrayAdapter<String> adapter;
    private ProgressBar progress;
    private TextView url;
    private View pinContainer;
    private ImageButton viewPassword;
    private ImageButton qr;

    public static ServerFragment newInstance() {
        return new ServerFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_server, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        model = new ViewModelProvider(this).get(ServerViewModel.class);

        model.addresses().observe(getViewLifecycleOwner(), list -> {
            String add = model.address().getValue();
            int index = list.indexOf(add);
            index = Math.max(index, 0);
            adapter.clear();
            adapter.addAll(list);
            adapter.notifyDataSetChanged();
            if(index > 0) {
                addresses.setSelection(index);
            }
            Log.i(TAG, "Network address updates: " + list);
        });

        model.password().observe(getViewLifecycleOwner(), s -> {
            password.setText(s.replaceAll(".", "*"));
        });

        model.url().observe(getViewLifecycleOwner(), s -> {
            url.setText(s);
        });

        model.username().observe(getViewLifecycleOwner(), s -> {
            username.setText(s);
        });

        model.httpPort().observe(getViewLifecycleOwner(), s -> {
            model.url().postValue("http://" + model.address().getValue() + ":" + model.httpPort().getValue() + "/");
        });

        model.httpPort().observe(getViewLifecycleOwner(), p -> {
            port.setText(p + "");
        });

        model.totp().observe(getViewLifecycleOwner(), b -> {
           if(b == null || b) {
               model.progress().observe(getViewLifecycleOwner(), i -> {
                   progress.setProgress(i);
               });
               model.pin().observe(getViewLifecycleOwner(), s -> {
                   pin.setText(s);
               });
               pinContainer.setVisibility(View.VISIBLE);
           } else {
               pinContainer.setVisibility(View.GONE);
           }
        });

        model.refresh();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        port = view.findViewById(R.id.port);
        addresses = view.findViewById(R.id.addresses);
        status = view.findViewById(R.id.status);
        statusIcon = view.findViewById(R.id.status_icon);
        action = view.findViewById(R.id.action);
        url = view.findViewById(R.id.url);
        qr = view.findViewById(R.id.url_qr);

        username = view.findViewById(R.id.username);
        password = view.findViewById(R.id.password);
        pin = view.findViewById(R.id.pin);
        progress = view.findViewById(R.id.pin_progress);
        progress.setMin(0);
        progress.setMax(30000);
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addresses.setAdapter(adapter);
        pinContainer = view.findViewById(R.id.pin_container);
        viewPassword = view.findViewById(R.id.show_password);
        viewPassword.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                password.setText(model.password().getValue());
                return true;
            }

            if(event.getAction() == MotionEvent.ACTION_UP) {
                password.setText(password.getText().toString().replaceAll(".", "*"));
                return true;
            }

            return false;
        });

        addresses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String address = adapter.getItem(position);
                model.address().postValue(address);
                App.app().executor().submit(()->{
                    App.app().db().configuration().save(new Configuration("address", address));
                });
                String port = model.httpPort().getValue() + "";
                if(address != null && !address.isEmpty() && !port.isEmpty()) {
                    model.url().postValue("http://" + address + ":" + port + "/");
                }
                Log.i(TAG, "Ip address selected: " + address);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        qr.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("text", model.url().getValue());
            Navigation.findNavController(v).navigate(R.id.nav_qr_code, bundle);
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.server_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Bundle bundle = new Bundle();

        switch (item.getItemId()) {
            case R.id.security:
                Log.i(TAG, "Navigate to web security dialog: " + bundle);
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                        .navigate(R.id.nav_auth, bundle);
                return true;
            case R.id.server:
                Log.i(TAG, "Navigate to server setting dialog: " + bundle);
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                        .navigate(R.id.nav_http_server_config, bundle);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}