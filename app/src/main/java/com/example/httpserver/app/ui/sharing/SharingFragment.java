package com.example.httpserver.app.ui.sharing;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.example.httpserver.R;
import com.example.httpserver.app.ui.sharing.contents.ContentsFragment;
import com.example.httpserver.app.ui.sharing.folders.FoldersFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class SharingFragment extends Fragment {
    public static final String TAG = SharingFragment.class.getName();

    private SharingViewModel mViewModel;

    public static SharingFragment newInstance() {
        return new SharingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sharing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TabLayout tabLayout = view.findViewById(R.id.tabs);
        ViewPager2 container = view.findViewById(R.id.sharing_container);
        container.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0:
                        return FoldersFragment.newInstance();
                    case 1:
                        return ContentsFragment.newInstance();
                    default:
                        throw new IllegalStateException();
                }
            }

            @Override
            public int getItemCount() {
                return 2;
            }
        });
        new TabLayoutMediator(tabLayout, container, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Folders");
                    break;
                case 1:
                    tab.setText("Contents");
                    break;
                default:
                    throw new IllegalStateException();
            }
        }).attach();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SharingViewModel.class);
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