package com.example.httpserver.app.ui.view.folders.folder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
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
import androidx.documentfile.provider.DocumentFile;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.example.httpserver.R;
import com.example.httpserver.app.App;
import com.example.httpserver.app.repository.entity.Folder;
import com.example.httpserver.app.ui.NavigationFragment;
import com.example.httpserver.common.FileUtils;
import com.example.httpserver.common.Utils;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.hbisoft.pickit.PickiT;
import com.hbisoft.pickit.PickiTCallbacks;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;
import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;

public class FolderFragment extends NavigationFragment {

    public static final String TAG = FolderFragment.class.getName();

    private FolderViewModel model;
    private TextView pathSummary;
    private TextView labelSummary;
    private SwitchMaterial read;
    private SwitchMaterial write;
    private SwitchMaterial subfolder;
    private SwitchMaterial share;
    private View delete;

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
        model = new ViewModelProvider(this).get(FolderViewModel.class);
        init();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.path_container).setOnClickListener(v -> {
            startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE), 0);
        });
        view.findViewById(R.id.context_container).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("title", "Set Context");
            bundle.putString("context", model.folder().name.getValue());
            Log.i(TAG, "Navigate to set context");
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.nav_context_editor, bundle);
        });
        pathSummary = view.findViewById(R.id.path_summary);
        labelSummary = view.findViewById(R.id.laebl_summary);
        read = view.findViewById(R.id.read);
        write = view.findViewById(R.id.write);
        subfolder = view.findViewById(R.id.subfolder);
        share = view.findViewById(R.id.share);

        delete = view.findViewById(R.id.delete);

        read.setOnClickListener(v -> {
            onSwitchRead(read.isChecked());
        });
        write.setOnClickListener(v -> {
            onSwitchWrite(write.isChecked());
        });
        subfolder.setOnClickListener(v -> {
            onSwitchRecursive(subfolder.isChecked());
        });
        share.setOnClickListener(v -> {
            onSwitchShare(share.isChecked());
        });
        delete.setOnClickListener(v -> {
            onDelete();
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
            Log.i(TAG, "Receive bundle: " + bundle);
            Folder folder = (Folder)bundle.get("folder");
            if(folder != null) {
                Log.i(TAG, "Receive folder: " + folder);
                model.folder(folder);
            }
            if(bundle.containsKey("context")) {
                Log.i(TAG, "Receive context: " + bundle.getString("context"));
                onSetContext(bundle.getString("context"));
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
        model.folder().share.observe(getViewLifecycleOwner(), b -> {
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

    private void onSwitchShare(boolean share) {
        model.folder().share.postValue(share);
    }

    private void onSave() {
        Folder folder = model.folder().toFolder(null);
        if("".equals(folder.path)) {
            error("Path is empty");
            Log.i(TAG, "Folder is empty: " + folder);
            return;
        }

        Path path = Paths.get(folder.path);
        if(!Files.exists(path) || !Files.isDirectory(path)) {
            error("Path is not found or is not a directory");
            Log.i(TAG, "Folder path is not found or is not a directory: " + folder);
            return;
        };
        if(!Pattern.matches("\\p{L}+", folder.name)) {
            error("Context must be a valid identifier: " + folder.name);
            Log.i(TAG, "Folder context name is not valid: " + folder);
            return;
        }

        App.app().executor().submit(()->{
            try {
                App.app().db().folder().save(folder);
                Log.i(TAG, "Save folder successfully: " + folder);
                requireActivity().runOnUiThread(this::back);
                Log.i(TAG, "Navigate back to folders");
            } catch (Exception e) {
                Log.e(TAG, "Failed to save folder: " + folder, e);
                requireActivity().runOnUiThread(()->error("Path or context may already added"));
            }
        });
    }

    private void onCancel() {
        back();
    }

    private void onDelete() {
        alert();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
            case R.id.cancel:
                onCancel();
                Log.d(TAG, "Cancel folder creation or modification");
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

    private void back() {
        NavController controller = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        controller.navigate(R.id.nav_folders, null, new NavOptions.Builder().setPopUpTo(R.id.nav_folders, true).build());
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

    private void alert() {
        AlertDialog dialog = new AlertDialog.Builder(getContext()).setMessage("Are you sure to delete this folder").create();
        dialog.setTitle("Delete " + model.folder().name.getValue());

        dialog.setCancelable(false);
        dialog.setButton(BUTTON_POSITIVE, "DELETE", (dialog1, which) -> App.app().executor().submit(()->{
            try {
                App.app().db().folder().remove(model.folder().toFolder(null));
                Log.i(TAG, "Delete folder: " + model.folder().toFolder(null));
            } catch (Exception e) {
                Log.e(TAG, "Filed to delete folder: " + model.folder().toFolder(null));
            }
            requireActivity().runOnUiThread(this::back);
        }));
        dialog.setButton(BUTTON_NEGATIVE, "CANCEL", (d, w) ->{});
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode == RESULT_OK && data != null) {
            onSelectPath(Utils.path(requireContext(), data.getData()));
            Log.i(TAG, "Path selected,\noriginal: " + data.getData() + "\nFile path: " + Utils.path(requireContext(), data.getData()));
        }
    }
}