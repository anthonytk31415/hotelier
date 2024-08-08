package com.tpd.staybooking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;

@Entity
@Table(name = "user")
@JsonDeserialize(builder = User.Builder.class)
public class User {
    @Id
    private String username;
    @JsonIgnore // The User class object can also be sent to the frontend. However, when
                // returning to the frontend, only the username is provided, not the password
                // and enabled information.
    private String password;
    @JsonIgnore
    private boolean enabled;

    public User() {
    } // It must be added, otherwise, it won’t compile. Below, the User constructor is
      // private, so it’s needed.

    private User(Builder builder) {
        this.username = builder.username;
        this.password = builder.password;
        this.enabled = builder.enabled;
    }

    // getters and setters below.
    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public User setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    // Why use a builder? With User user = new User("abc", "123", "true"), we don't
    // know which input corresponds to which key. The builder here belongs to this
    // class
    public static class Builder { // You can use the fluent API. Without a builder, you would need a constructor.
                                  // The builder pattern is used to address issues with naming in Java.
                                  // argument issue
        @JsonProperty("username")
        private String username;

        @JsonProperty("password")
        private String password;

        @JsonProperty("enabled")
        private boolean enabled;

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setEnabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}

// User cannot use a record class because some annotations used later are not
// compatible with the record class.