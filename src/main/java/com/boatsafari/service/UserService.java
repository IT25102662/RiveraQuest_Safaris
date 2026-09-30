package com.boatsafari.service;

import com.boatsafari.dto.LoginRequestDTO;
import com.boatsafari.dto.LoginResponseDTO;
import com.boatsafari.model.User;

import java.util.List;

public interface UserService {
    User registerUser(User user);
    LoginResponseDTO login(LoginRequestDTO loginDTO);
    List<User> getAllUsers();
    User getUserById(Long id);
    User updateUser(Long id, User updatedUser);
    void deleteUser(Long id);
    User updateUserStatus(Long id, String status);
}
