package com.example.httpserver.app.ui.view.folders;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.httpserver.app.App;
import com.example.httpserver.app.repository.entity.Folder;

import java.util.List;

public class FoldersViewModel extends ViewModel {

    public static final String TAG = FoldersViewModel.class.getName();

    private final MutableLiveData<List<Folder>> folders;

    public FoldersViewModel() {
        folders = new MutableLiveData<>();
    }

    public LiveData<List<Folder>> folders() {
        App.app().executor().submit(()-> {
            try {
                folders.postValue(App.app().db().folder().gets());
            } catch (Exception e) {
                Log.e(TAG, "Failed to load folder records from database", e);
            }
        });
        return folders;
    }

}