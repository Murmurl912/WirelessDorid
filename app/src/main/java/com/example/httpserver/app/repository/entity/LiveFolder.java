package com.example.httpserver.app.repository.entity;

import androidx.lifecycle.MutableLiveData;

import java.util.Objects;

public class LiveFolder {
    public final MutableLiveData<String> path = new MutableLiveData<>("");
    public final MutableLiveData<String> name = new MutableLiveData<>("");
    public final MutableLiveData<Boolean> write = new MutableLiveData<>(false);
    public final MutableLiveData<Boolean> read = new MutableLiveData<>(true);
    public final MutableLiveData<Boolean> recursive = new MutableLiveData<>(true);
    public final MutableLiveData<Boolean> share = new MutableLiveData<>(true);

    protected LiveFolder(Folder folder) {
        path.postValue(folder.path);
        name.postValue(folder.name);
        write.postValue(folder.write);
        read.postValue(folder.read);
        recursive.postValue(folder.recursive);
        share.postValue(folder.share);
    }

    public Folder toFolder(Folder folder) {
        folder = folder == null ? new Folder() : folder;
        folder.name = name.getValue();
        folder.path = Objects.requireNonNull(path.getValue());
        folder.read = Objects.requireNonNull(read.getValue());
        folder.write = Objects.requireNonNull(write.getValue());
        folder.recursive = Objects.requireNonNull(recursive.getValue());
        folder.share = Objects.requireNonNull(share.getValue());
        return folder;
    }

    public static LiveFolder from(Folder folder) {
        return new LiveFolder(folder);
    }

}
