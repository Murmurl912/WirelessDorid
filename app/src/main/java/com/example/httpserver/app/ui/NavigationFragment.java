package com.example.httpserver.app.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.httpserver.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

public abstract class NavigationFragment extends Fragment {

    public static final String TAG = NavigationFragment.class.getName();

    public NavigationFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main_menu, menu);
        SwitchMaterial server = (menu.findItem(R.id.server_switch).getActionView().findViewById(R.id.server_switch_button));
        if(server != null) {
            Log.i(TAG, "Observer server status on menu switch");

            server.setOnClickListener(v -> {
                try {
                    boolean checked = server.isChecked();
                    if(checked) {
                        Log.i(TAG, "Start http service from menu switch");
                    } else {
                        Log.i(TAG, "Stop http service from menu switch");
                    }
                } catch (Exception e) {

                }
            });
        }
    }


}