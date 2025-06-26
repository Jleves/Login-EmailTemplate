package com.Login.Email.Service;


import com.Login.Email.Model.User;

public interface UserService {

    User saveUser (User user);
    Boolean verifyToken(String token);
}
