package com.example.httpserver.app.ui.folders.folder;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.*;
import com.example.httpserver.R;
import com.example.httpserver.app.App;
import com.example.httpserver.app.adapter.repository.FolderRepository;
import com.example.httpserver.app.adapter.repository.entity.Folder;
import com.example.httpserver.app.database.AppDatabase;

public class FolderSettingActivity extends AppCompatActivity {

    private String path;
    private SettingsFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.folder_setting_activity);
        path = getIntent().getStringExtra("path");
        fragment = new SettingsFragment(path);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, fragment)
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getIntent().getStringExtra(path == null ? "Share a new folder" : path));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(path == null) {
            getMenuInflater().inflate(R.menu.folder_menu, menu);
            return true;

        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            case R.id.cancel:
                fragment.onCancel();
                break;
            case R.id.apply:
                fragment.onSave();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        private final Folder folder;
        private final boolean isCreation;

        public SettingsFragment(String path) {
            if (path != null && !path.equals("")) {
                isCreation = false;
                folder = App.db().getFolderRepository().get(path);
            } else {
                isCreation = true;
                folder = new Folder();
            }
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.folder_preferences, rootKey);
            PreferenceManager preferenceManager = getPreferenceManager();
            preferenceManager.setPreferenceDataStore(new DataStore(folder));
            findPreference("path").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Log.d("INTENT", newValue.toString());
                    return false;
                }
            });
        }

        @Override
        public boolean onPreferenceTreeClick(Preference preference) {
            return super.onPreferenceTreeClick(preference);
        }

        public void onSave() {
            new Thread(()->{
                if(isCreation) {
                    App.db().getFolderRepository().add(folder);
                } else {
                    App.db().getFolderRepository().update(folder);
                }
                requireActivity().finish();
            }).start();
        }

        public void onCancel() {
            requireActivity().finish();
        }

    }

    public static class DataStore extends PreferenceDataStore {

        private final Folder folder;

        public DataStore(Folder folder) {
            this.folder = folder;
        }

        @Override
        public void putString(String key, @Nullable String value) {
            switch (key) {
                case "name":
                    folder.name = value;
                    break;
                case "path":
                    folder.path = value == null? "" : value;
                    break;
            }
        }

        @Override
        public void putBoolean(String key, boolean value) {
            switch (key) {
                case "read":
                    folder.read = value;
                    break;
                case "write":
                    folder.write = value;
                    break;
                case "child":
                    folder.child = value;
                    break;
                case "recursive":
                    folder.recursive = value;
                    break;
            }

        }

        @Nullable
        @Override
        public String getString(String key, @Nullable  String defValue) {
            switch (key) {
                case "name":
                    return folder.name;
                case "path":
                    return folder.path;
                default:
                    return defValue;
            }
        }

        @Override
        public boolean getBoolean(String key, boolean defValue) {
            switch (key) {
                case "read":
                    return folder.read;
                case "write":
                    return folder.write;
                case "child":
                    return folder.child;
                case "recursive":
                    return folder.recursive;
                default:
                    return defValue;
            }
        }
    }
}