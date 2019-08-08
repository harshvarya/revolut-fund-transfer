package com.revolut.fundtransfer.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class User {

    @JsonProperty(required = false)
    private long userId;


    @JsonProperty(required = true)
    private String name;


    @JsonProperty(required = true)
    private String emailId;


    public User() {
    }

    public User(String name, String emailAddress) {
        this.name = name;
        this.emailId = emailAddress;
    }

    public User(long userId, String userName, String emailId) {
        this.userId = userId;
        this.name = userName;
        this.emailId = emailId;
    }

    public long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public void setUserId(long userId) {

        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId == user.userId &&
                Objects.equals(name, user.name) &&
                Objects.equals(emailId, user.emailId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, name, emailId);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", emailAddress='" + emailId + '\'' +
                '}';
    }
}
