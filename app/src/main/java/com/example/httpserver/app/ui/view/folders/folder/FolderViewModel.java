package com.example.httpserver.app.ui.view.folders.folder;

import androidx.lifecycle.ViewModel;

import com.example.httpserver.app.repository.entity.Folder;
import com.example.httpserver.app.repository.entity.LiveFolder;

public class FolderViewModel extends ViewModel {

    private LiveFolder folder;
    public FolderViewModel() {
        folder = LiveFolder.from(new Folder());
    }

    public LiveFolder folder() {
        return folder;
    }

    public void folder(Folder folder) {
        this.folder = LiveFolder.from(folder);
    }
}