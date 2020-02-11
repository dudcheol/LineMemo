package com.example.linememo;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Memo implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String content;
    private String imageURI;
    private long date;

    public Memo(String title, String content, String imageURI, long date) {
        this.title = title;
        this.content = content;
        this.imageURI = imageURI;
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

    public String getImageURI() {
        return imageURI;
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

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @NonNull
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Memo{ ");
        sb.append("title=").append(title);
        sb.append(", content=").append(content);
        sb.append(", image=").append(imageURI);
        sb.append("}");
        return sb.toString();
    }
}
