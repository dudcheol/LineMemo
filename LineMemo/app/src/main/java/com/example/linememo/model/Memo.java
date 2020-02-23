package com.example.linememo.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Memo implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String content;
    private List<String> imageUris;
    private long date;

    public Memo(String title, String content, List<String> imageUris, long date) {
        this.title = title;
        this.content = content;
        this.imageUris = imageUris == null ? new ArrayList<String>() : imageUris;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public List<String> getImageUris() {
        return imageUris;
    }

    public long getDate() {
        return date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setImageUri(List<String> imageUris) {
        if (imageUris != null) this.imageUris = imageUris;
        else this.imageUris = new ArrayList<>();
    }

    public void setDate(long date) {
        this.date = date;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("id=").append(id).append(", ");
        sb.append("title=").append(title).append(", ");
        sb.append("content=").append(content).append(", ");
        sb.append("imageUri=").append(imageUris).append(", ");
        sb.append("date=").append(date);
        return sb.toString();
    }
}
