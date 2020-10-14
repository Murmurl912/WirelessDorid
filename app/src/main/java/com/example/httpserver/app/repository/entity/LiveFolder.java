package com.example.httpserver.app.repository.entity;

import android.widget.Button;
import androidx.lifecycle.MutableLiveData;

import java.util.Objects;

public class LiveFolder {
    public final MutableLiveData<Long> id = new MutableLiveData<>(-1L);
    public final MutableLiveData<String> path = new MutableLiveData<>("");
    public final MutableLiveData<String> name = new MutableLiveData<>("");
    public final MutableLiveData<Boolean> write = new MutableLiveData<>(false);
    public final MutableLiveData<Boolean> read = new MutableLiveData<>(true);
    public final MutableLiveData<Boolean> recursive = new MutableLiveData<>(true);
    public final MutableLiveData<Boolean> share = new MutableLiveData<>(true);

    protected LiveFolder(Folder folder) {
        id.postValue(folder.id);
        path.postValue(folder.path);
        name.postValue(folder.name);
        write.postValue(folder.write);
        read.postValue(folder.read);
        recursive.postValue(folder.recursive);
        share.postValue(folder.share);
    }

    public Folder toFolder(Folder folder) {
        folder = folder == null ? new Folder() : folder;
        Long l = id.getValue();
        folder.id = l == null ? -1L : l;
        folder.name = name.getValue();
        String s = path.getValue();
        folder.path = s == null ? "" : s;
        Boolean b = read.getValue();
        folder.read = b != null && b;
        b = read.getValue();
        folder.write = b != null && b;
        b = write.getValue();
        folder.recursive = b != null && b;
        b = recursive.getValue();
        folder.share = b != null && b;
        b = share.getValue();
        return folder;
    }

    public static LiveFolder from(Folder folder) {
        return new LiveFolder(folder);
    }

}
