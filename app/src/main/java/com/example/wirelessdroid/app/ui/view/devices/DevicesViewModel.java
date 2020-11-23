package com.example.wirelessdroid.app.ui.view.devices;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DevicesViewModel extends ViewModel {

    private final MutableLiveData<List<Map<String, String>>> devices = new MutableLiveData<>();

    public LiveData<List<Map<String, String>>> device() {
        devices.postValue(devices());
        return devices;
    }

    public void refresh() {
        device();
    }

    private List<Map<String, String>> devices() {
        List<Map<String, String>> devices = new ArrayList<>();

        Map<String, String> raspberry = new HashMap<>();
        raspberry.put("name", "Raspberry Pi");
        raspberry.put("description", "Disconnected");
        devices.add(raspberry);
        return devices;
    }
}