package com.studentmanagement.services;

import com.studentmanagement.models.User;

import java.util.List;

public class AuthService {
    private static User currentUser;
    
    public static boolean login(String username, String password) {
        List<User> users = JsonDataService.loadUsers();
        
        for (User user : users) {
            if (user.getUsername().equals(username) && 
                user.getPassword().equals(password)) {
                currentUser = user;
                return true;
            }
        }
        return false;
    }
    
    public static void logout() {
        currentUser = null;
    }
    
    public static User getCurrentUser() {
        return currentUser;
    }
    
    public static boolean isAdmin() {
        return currentUser != null && "admin".equals(currentUser.getRole());
    }
}
