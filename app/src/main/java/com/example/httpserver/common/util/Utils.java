package com.example.httpserver.common.util;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.DocumentsContract;
import androidx.annotation.NonNull;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

public class Utils {

    public static String path(Context context, Uri tree) {
        String name = volume(context, tree);
        String mount = mount(context, name);
        String path = path(tree);
        if (mount.endsWith("/") && path.startsWith("/")) {
            return mount + path.substring(1, path.length());
        }
        if (!mount.endsWith("/") && !path.startsWith("/")) {
            return mount + "/" + path;
        }
        return mount + path;
    }

    private static String volume(Context context, Uri tree) {
        String id = DocumentsContract.getTreeDocumentId(tree);
        String[] parts = id.split(":");
        if (parts.length > 0) {
            return parts[0];
        } else {
            return "";
        }
    }

    @NonNull
    private static String mount(@NonNull Context context, @NonNull String name) {
        StorageManager storageManager = context.getSystemService(StorageManager.class);
        StorageVolume primaryVolume = storageManager.getPrimaryStorageVolume();
        if (name.contains("primary")) {
            return path(primaryVolume);
        }

        List<StorageVolume> volumeList = storageManager.getStorageVolumes();
        for (StorageVolume v : volumeList) {
            String uuid = v.getUuid();
            if (Objects.equals(uuid, name)) {
                return path(v);
            }
        }

        return File.separator;
    }

    @NonNull
    private static String path(@NonNull StorageVolume volume) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            File dir = volume.getDirectory();
            return dir == null ? File.separator : dir.getAbsolutePath();
        } else {
            try {
                Method getPath = StorageVolume.class.getMethod("getPath");
                Object dir = getPath.invoke(volume);
                return dir == null ? "" : dir.toString();
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                return File.separator;
            }
        }
    }

    private static String path(final Uri treeUri) {
        final String docId = DocumentsContract.getTreeDocumentId(treeUri);
        final String[] split = docId.split(":");
        if ((split.length >= 2) && (split[1] != null)) return split[1];
        else return File.separator;
    }
}
