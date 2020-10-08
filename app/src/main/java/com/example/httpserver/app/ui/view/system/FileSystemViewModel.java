package com.example.httpserver.app.ui.view.system;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.httpserver.app.App;

import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class FileSystemViewModel extends ViewModel {

    public LiveData<String> data() {
        MutableLiveData<String> data = new MutableLiveData<>();
        App.app().executor().submit(()->{
            FileSystem system = FileSystems.getDefault();
            StringBuffer str = new StringBuffer();
            str.append("Provider: ")
                    .append(system.provider().getScheme())
                    .append("\n")
                    .append("Roots: ")
                    .append(roots(system.getRootDirectories()))
                    .append("\n")
                    .append("Stores: ")
                    .append(stores(system.getFileStores()))
                    .append("\n");
            data.postValue(str.toString());
        });
        return data;
    }

    public static String stores(Iterable<FileStore> stores) {
        String info = "";
        for (FileStore store : stores) {
            info += "\n\tname: " + store.name();
            info += "\n\ttype: " + store.type();
            info += "\n";
        }
        return info;
    }

    public static String roots(Iterable<Path> paths) {
        String info = "";
        for (Path path : paths) {
            info += "\n\tname: " + path.getFileName();
            info += "\n\turi: " + path.toUri();
            info += "\n";
        }
        return info;
    }
}