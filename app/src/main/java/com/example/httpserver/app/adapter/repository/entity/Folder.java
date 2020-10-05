package com.example.httpserver.app.adapter.repository.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class Folder {
    @NonNull
    @PrimaryKey
    public String path = "";
    public String name;
    public boolean write = false;
    public boolean read = true;
    public boolean recursive = true;
    public boolean child = true;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Folder folder = (Folder) o;
        return path.equals(folder.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}
