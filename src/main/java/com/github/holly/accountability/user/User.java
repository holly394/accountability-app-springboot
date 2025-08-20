package com.github.holly.accountability.user;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name="user")
public class User {
    private String username;
    private String name;
    private String password;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public User() {

    }
    public User(String username, String name, String password) {
        this.username = username;
        this.name = name;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username) && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, id);
    }
}
