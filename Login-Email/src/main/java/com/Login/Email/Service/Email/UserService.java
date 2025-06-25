package com.Login.Email.Service.Email;


import com.Login.Email.Model.User;

public interface UserService {

    User saveUser (User user);
    Boolean verifyToken(String token);
}
