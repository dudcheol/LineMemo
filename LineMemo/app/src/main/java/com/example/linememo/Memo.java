package com.example.linememo;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class Memo {
    @PrimaryKey(autoGenerate = true)
    private String title;
    private String content;
    private String imageURI;
    private Date date;

    public Memo(String title, String content, String imageURI) {
        this.title = title;
        this.content = content;
        this.imageURI = imageURI;
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

    public Date getDate() {
        return date;
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

    public void setDate(Date date) {
        this.date = date;
    }

    @NonNull
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Memo{ ");
        sb.append("title=").append(title);
        sb.append(", content=").append(content);
        sb.append(", image=").append(imageURI);
        sb.append(", date=").append(date.toString());
        sb.append("}");
        return super.toString();
    }
}
