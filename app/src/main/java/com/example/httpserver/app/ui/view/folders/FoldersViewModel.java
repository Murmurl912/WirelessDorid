package com.example.httpserver.app.ui.view.folders;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.httpserver.app.App;
import com.example.httpserver.app.repository.entity.Folder;

import java.util.List;

public class FoldersViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    private final MutableLiveData<List<Folder>> folders;

    public FoldersViewModel() {
        folders = new MutableLiveData<>();
    }

    public LiveData<List<Folder>> folders() {
        App.app().executor().submit(()-> {
            folders.postValue(App.app().db().folder().gets());
        });
        return folders;
    }

}