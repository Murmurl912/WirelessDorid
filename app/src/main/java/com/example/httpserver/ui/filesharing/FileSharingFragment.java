package com.example.httpserver.ui.filesharing;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.example.httpserver.R;
import com.google.android.material.tabs.TabLayout;

public class FileSharingFragment extends Fragment {
    public static final String TAG = FileSharingFragment.class.getName();

    private FileSharingViewModel mViewModel;
    private NavHostFragment fragment;

    public static FileSharingFragment newInstance() {
        return new FileSharingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_file_sharing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragment = (NavHostFragment)getChildFragmentManager().findFragmentById(R.id.nav_file_host_fragment);

        TabLayout tabLayout = view.findViewById(R.id.tabs);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        fragment.getNavController()
                        .navigate(R.id.nav_file_contents);
                        break;
                    case 1:
                        fragment.getNavController()
                                .navigate(R.id.nav_file_folders);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(FileSharingViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onPrimaryNavigationFragmentChanged(boolean isPrimaryNavigationFragment) {
        super.onPrimaryNavigationFragmentChanged(isPrimaryNavigationFragment);
        Log.d(TAG, "Primary Navigation Fragment Channged: " + isPrimaryNavigationFragment);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }
}