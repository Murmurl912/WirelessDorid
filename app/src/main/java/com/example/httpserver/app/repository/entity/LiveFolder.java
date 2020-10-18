package com.example.httpserver.app.repository.entity;

import androidx.lifecycle.MutableLiveData;

public class LiveFolder {
    public final MutableLiveData<Long> id = new MutableLiveData<>();
    public final MutableLiveData<String> path = new MutableLiveData<>("");
    public final MutableLiveData<String> name = new MutableLiveData<>("");
    public final MutableLiveData<Boolean> write = new MutableLiveData<>(false);
    public final MutableLiveData<Boolean> read = new MutableLiveData<>(true);
    public final MutableLiveData<Boolean> publicly = new MutableLiveData<>(false);
    public final MutableLiveData<Boolean> share = new MutableLiveData<>(true);

    protected LiveFolder(Folder folder) {
        id.postValue(folder.id);
        path.postValue(folder.path);
        name.postValue(folder.name);
        write.postValue(folder.write);
        read.postValue(folder.read);
        publicly.postValue(folder.publicly);
        share.postValue(folder.share);
    }

    public Folder toFolder(Folder folder) {
        folder = folder == null ? new Folder() : folder;
        folder.id = id.getValue();
        folder.name = name.getValue();
        String s = path.getValue();
        folder.path = s == null ? "" : s;

        Boolean b = read.getValue();
        folder.read = b != null && b;

        b = write.getValue();
        folder.write = b != null && b;

        b = publicly.getValue();
        folder.publicly = b != null && b;

        b = share.getValue();
        folder.share = b != null && b;

        return folder;
    }

    public static LiveFolder from(Folder folder) {
        return new LiveFolder(folder);
    }

}
