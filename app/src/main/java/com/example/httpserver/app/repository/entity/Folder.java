package com.example.httpserver.app.repository.entity;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Folder implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public Long id;
    public String path;
    public String name;
    public boolean write = false;
    public boolean read = true;
    public boolean share = true;
    public boolean publicly = false;

    public Folder() {

    }

    protected Folder(Parcel in) {
        id = in.readLong();
        id = id == -1 ? null : id;
        path = in.readString();
        name = in.readString();
        write = in.readByte() != 0;
        read = in.readByte() != 0;
        publicly = in.readByte() != 0;
        share = in.readByte() != 0;
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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id == null ? -1 : id);
        dest.writeString(path);
        dest.writeString(name);
        dest.writeByte((byte) (write ? 1 : 0));
        dest.writeByte((byte) (read ? 1 : 0));
        dest.writeByte((byte) (publicly ? 1 : 0));
        dest.writeByte((byte) (share ? 1 : 0));
    }

    @Override
    public String toString() {
        return "Folder{" +
                "id=" + id +
                ", path='" + path + '\'' +
                ", name='" + name + '\'' +
                ", write=" + write +
                ", read=" + read +
                ", publicly=" + publicly +
                ", share=" + share +
                '}';
    }
}
