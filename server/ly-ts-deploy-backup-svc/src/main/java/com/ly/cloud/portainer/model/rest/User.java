package com.ly.cloud.portainer.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class User {

    @JsonProperty("Username")
    String username;

    @JsonProperty("userTheme")
    String userTheme;

    @JsonProperty("password")
    String password;

    @JsonProperty("role")
    Integer role;

    @JsonProperty("Id")
    Integer id;

    public User() {
    }

    public User(String username, String password, Integer role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
}
