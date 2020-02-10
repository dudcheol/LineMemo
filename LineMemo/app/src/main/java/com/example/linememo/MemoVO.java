package com.example.linememo;

public class MemoVO {
    private String title;
    private String content;
    private String imageURI;

    public MemoVO(String title, String content, String imageURI) {
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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }
}
