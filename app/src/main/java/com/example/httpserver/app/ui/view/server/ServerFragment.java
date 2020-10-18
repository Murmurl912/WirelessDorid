package com.example.httpserver.app.ui.view.server;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.example.httpserver.app.repository.entity.ServerConfig;
import com.example.httpserver.app.services.HttpService;
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

        App.app().serverStatus().observe(getViewLifecycleOwner(), s -> {
            if(s == null) {
                return;
            }
            int strId = R.string.status_stopped;
            int id = R.drawable.ic_gray;
            switch (s) {
                case "stopped":
                    action.setText(R.string.start_server);
                    id = R.drawable.ic_gray;
                    strId = R.string.status_stopped;
                    break;
                case "starting":
                    id = R.string.status_starting;
                    id = R.drawable.ic_yellow;
                    break;
                case "stopping":
                    id = R.drawable.ic_yellow;
                    strId = R.string.status_stopping;
                    break;
                case "running":
                    action.setText(R.string.stop_server);
                    id = R.drawable.ic_green;
                    strId = R.string.status_running;
                    break;
            }
            status.setText(strId);
            statusIcon.setImageResource(id);
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

        action.setOnClickListener(v -> {
            String s = App.app().serverStatus().getValue();
            if(s == null) {
                s = requireContext().getString(R.string.status_stopped);
            }
            switch (s) {
                case "error":
                case "stopped":
                    Log.i(TAG, "Start http service, server status: " + s);
                    requireActivity().startService(new Intent(requireContext(), HttpService.class));
                    break;
                case "running":
                    Log.i(TAG, "Stop http service, server status: " + s);
                    requireActivity().stopService(new Intent(requireContext(), HttpService.class));
                    break;
            }
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
        ServerConfig config = new ServerConfig();
        config.username = model.username().getValue();
        config.password = model.password().getValue();
        Boolean totp = model.totp().getValue();
        Boolean tls = model.totp().getValue();
        Boolean basic = model.basic().getValue();
        config.totp = totp == null || totp;
        config.tls = tls == null || tls;
        config.basic = basic == null || basic;
        Integer ftport = model.ftpPort().getValue();
        if(ftport != null) {
            config.ftp_port = ftport;
        }
        Integer httpport = model.httpPort().getValue();
        if(httpport != null) {
            config.http_port = httpport;
        }
        Boolean ftp = model.ftp().getValue();
        config.ftp = ftp != null ? ftp : false;
        Boolean http = model.http().getValue();
        config.http = http != null ? http : true;
        bundle.putParcelable("server_config", config);

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