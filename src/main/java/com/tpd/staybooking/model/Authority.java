package com.tpd.staybooking.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "authority")
public class Authority { // It can also be written as a record, so you don't need to write getters, setters, or override @equals and @toString

    @Id
    private String username;
    private String authority;

    public Authority() {
    }

    public Authority(String username, String authority) {
        this.username = username;
        this.authority = authority;
    }

    public String getUsername() {
        return username;
    }

    public Authority setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getAuthority() {
        return authority;
    }

    public Authority setAuthority(String authority) {
        this.authority = authority;
        return this;
    }

    // Not using a builder because this class object is not used much.
}