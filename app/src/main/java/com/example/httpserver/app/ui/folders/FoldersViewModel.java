package com.example.httpserver.app.ui.folders;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.httpserver.app.App;
import com.example.httpserver.app.adapter.repository.entity.Folder;

import java.util.List;

public class FoldersViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    private MutableLiveData<List<Folder>> folders;

    public FoldersViewModel() {
        folders = new MutableLiveData<>();
    }

    public LiveData<List<Folder>> folders() {
        new Thread(()->{
            folders.postValue(App.db().getFolderRepository().get());
        }).start();
        return folders;
    }

}