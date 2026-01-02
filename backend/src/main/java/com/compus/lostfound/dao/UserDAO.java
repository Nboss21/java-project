package com.campus.lostfound.dao;

import com.campus.lostfound.model.User;

public interface UserDAO {
    void createUser(User user);
    User findByUsername(String username);
    User findByEmail(String email);
}