package com.example.linememo;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Memo {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String content;
    private String imageURI;

    public Memo(String title, String content, String imageURI) {
        this.title = title;
        this.content = content;
        this.imageURI = imageURI;
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
