package com.ly.cloud.backup.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public class OpenvasGsadResponseDto {
    @XStreamAlias("title")
    private String title;
    @XStreamAlias("message")
    private String message;
    @XStreamAlias("token")
    private String token;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "OpenvasGsadResponseDto{" +
                "title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
