package com.example.httpserver.app.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.httpserver.app.ui.view.devices.DevicesFragment;
import com.example.httpserver.app.ui.view.services.ServicesFragment;

public class HomePageAdapter extends FragmentStateAdapter {

    public HomePageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public HomePageAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return ServicesFragment.newInstance();
            case 1:
                return DevicesFragment.newInstance();
            default:
                // todo handle default case
                throw new IllegalStateException("Invalid position");
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
