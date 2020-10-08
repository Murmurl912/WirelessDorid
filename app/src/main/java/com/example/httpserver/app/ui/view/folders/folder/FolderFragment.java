package com.example.httpserver.app.ui.view.folders.folder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.httpserver.R;
import com.example.httpserver.app.App;
import com.example.httpserver.app.repository.entity.Folder;
import com.example.httpserver.app.ui.NavigationFragment;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class FolderFragment extends NavigationFragment {

    public static final String TAG = FolderFragment.class.getName();

    private FolderViewModel model;
    private TextView pathSummary;
    private TextView labelSummary;
    private SwitchMaterial read;
    private SwitchMaterial write;
    private SwitchMaterial subfolder;

    public static FolderFragment newInstance() {
        return new FolderFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_folder, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        model = new ViewModelProvider(requireActivity()).get(FolderViewModel.class);
        init();
        Bundle bundle = getArguments();
        Log.d(TAG, "Bundle: " + bundle);
        if(bundle == null) {
            return;
        }
        if(bundle.containsKey("title")) {
            String title = bundle.getString("title");
            switch (title) {
                case "Choose Path":
                    onSelectPath(bundle.getString("text"));
                    break;
                case "Set Context":
                    onSetContext(bundle.getString("text"));
                    break;
            }
            bundle.remove("title");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.path_container).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("title", "Choose Path");
            bundle.putString("text", "");
            Navigation.findNavController(v).navigate(R.id.nav_path_chooser, bundle);
        });
        view.findViewById(R.id.context_container).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("title", "Set Context");
            bundle.putString("text", "");
            Navigation.findNavController(v).navigate(R.id.nav_context_editor, bundle);
        });
        pathSummary = view.findViewById(R.id.path_summary);
        labelSummary = view.findViewById(R.id.laebl_summary);
        read = view.findViewById(R.id.read);
        write = view.findViewById(R.id.write);
        subfolder = view.findViewById(R.id.subfolder);

        read.setOnClickListener(v -> {
            onSwitchRead(read.isChecked());
        });
        write.setOnClickListener(v -> {
            onSwitchWrite(write.isChecked());
        });
        subfolder.setOnClickListener(v -> {
            onSwitchRecursive(subfolder.isChecked());
        });

    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        requireActivity().invalidateOptionsMenu();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void init() {
        Bundle bundle = getArguments();
        if(bundle != null) {
            Folder folder = (Folder)bundle.get("folder");
            if(folder != null) {
                model.folder(folder);
            }
        }
        model.folder().name.observe(getViewLifecycleOwner(), s -> {
            labelSummary.setText(s);
        });
        model.folder().path.observe(getViewLifecycleOwner(), s -> {
            pathSummary.setText(s);
        });
        model.folder().read.observe(getViewLifecycleOwner(), b -> {
            read.setChecked(b);
        });
        model.folder().write.observe(getViewLifecycleOwner(), b -> {
            write.setChecked(b);
        });
        model.folder().recursive.observe(getViewLifecycleOwner(), b -> {
            subfolder.setChecked(b);
        });
    }

    private void onSetContext(String context) {
        model.folder().name.postValue(context);
    }

    private void onSelectPath(String path) {
        model.folder().path.postValue(path);
    }

    private void onSwitchRead(boolean read) {
        model.folder().read.postValue(read);
    }

    private void onSwitchWrite(boolean write) {
        model.folder().write.postValue(write);
        if(write) {
            model.folder().read.postValue(true);
        }

    }

    private void onSwitchRecursive(boolean recursive) {
        model.folder().recursive.postValue(recursive);

    }

    private void onSave() {
        Folder folder = model.folder().toFolder(null);
        if("".equals(folder.path)) {
            // empty path
            error("Path is empty");
            return;
        }
        Path path = Paths.get(folder.path);
        if(!Files.exists(path) || !Files.isDirectory(path)) {
            error("Path is not found or is not a directory");
            return;
        };

        App.app().executor().submit(()->{
            try {
                App.app().db().folder().save(folder);
                requireActivity().runOnUiThread(()->back(getView()));
            } catch (Exception e) {
                error("Path may already added");
            }
        });
    }

    private void onCancel() {
        back(getView());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
            case R.id.cancel:
                onCancel();
                return true;
            case R.id.apply:
                onSave();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.folder_menu, menu);
    }

    private void back(View view) {
        Navigation.findNavController(view).navigate(R.id.nav_folders);
    }

    private void error(String message) {
        AlertDialog dialog = new AlertDialog.Builder(getContext()).setMessage(message).create();
        dialog.setTitle("Error");
        dialog.setCancelable(true);
        dialog.setButton(BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}