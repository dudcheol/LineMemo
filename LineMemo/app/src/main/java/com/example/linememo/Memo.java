package com.example.linememo;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
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
        this.imageUris = imageUris;
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
        this.imageUris = imageUris;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("title=" + title + ", ");
        sb.append("content=" + content + ", ");
        sb.append("imageUri=" + imageUris + ", ");
        sb.append("date=" + date);
        return sb.toString();
    }
}
