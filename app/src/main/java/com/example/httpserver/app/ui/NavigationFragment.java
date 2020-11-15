package com.example.httpserver.app.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

    }


}