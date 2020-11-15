package com.example.httpserver.app.ui.view.storage;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Random;

public class StorageViewModel extends ViewModel {

    private MutableLiveData<Long> time = new MutableLiveData<>();
    private Thread thread;

    public StorageViewModel() {

    }

    private void start() {
        if (thread != null) {
            thread.interrupt();
        }
        (thread = new Thread(() -> {
            Random random = new Random();
            while (true) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {
                    break;
                }
                time.postValue(System.currentTimeMillis());
            }
        })).start();
    }

    public LiveData<Long> time() {
        start();
        return time;
    }

    ;

    @Override
    protected void onCleared() {
        super.onCleared();
        if (thread != null) {
            thread.interrupt();
        }
    }
}