package com.example.httpserver.app.repository.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class Folder implements Parcelable {
    @NonNull
    @PrimaryKey
    public String path = "";
    public String name;
    public boolean write = false;
    public boolean read = true;
    public boolean recursive = true;

    public Folder() {

    }

    protected Folder(Parcel in) {
        path = in.readString();
        name = in.readString();
        write = in.readByte() != 0;
        read = in.readByte() != 0;
        recursive = in.readByte() != 0;
    }

    public static final Creator<Folder> CREATOR = new Creator<Folder>() {
        @Override
        public Folder createFromParcel(Parcel in) {
            return new Folder(in);
        }

        @Override
        public Folder[] newArray(int size) {
            return new Folder[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(name);
        dest.writeByte((byte) (write ? 1 : 0));
        dest.writeByte((byte) (read ? 1 : 0));
        dest.writeByte((byte) (recursive ? 1 : 0));
    }
}
