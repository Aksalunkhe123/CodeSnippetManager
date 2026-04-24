package com.snippetmanager.service;

import com.snippetmanager.dao.UserDAO;
import com.snippetmanager.model.User;
import com.snippetmanager.utils.PasswordHasher;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.Optional;

public class UserService {
    
    private final UserDAO userDAO;
    private User currentUser;
    
    public UserService() {
        this.userDAO = new UserDAO();
    }
    
    public User signup(String username, String email, String password) throws IllegalArgumentException {
        validateSignupInput(username, email, password);
        
        if (userDAO.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        if (userDAO.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }
        
        String passwordHash = PasswordHasher.hashPassword(password);
        
        User user = User.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordHash)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        return userDAO.save(user);
    }
    
    public Optional<User> login(String username, String password) {
        Optional<User> userOpt = userDAO.findByUsername(username);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String storedHash = user.getPasswordHash();
            System.out.println("DEBUG: Found user: " + username + ", hash: " + (storedHash != null ? storedHash.substring(0, Math.min(20, storedHash.length())) + "..." : "null"));
            if (PasswordHasher.verifyPassword(password, storedHash)) {
                this.currentUser = user;
                System.out.println("User logged in successfully: " + username);
                return Optional.of(user);
            }
        } else {
            System.out.println("DEBUG: User not found: " + username);
        }
        
        System.out.println("Failed login attempt for username: " + username);
        return Optional.empty();
    }
    
    public void logout() {
        if (currentUser != null) {
            System.out.println("User logged out: " + currentUser.getUsername());
            this.currentUser = null;
        }
    }
    
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public ObjectId getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : null;
    }
    
    public User updateUser(String username, String email) throws IllegalArgumentException {
        if (currentUser == null) {
            throw new IllegalStateException("No user is currently logged in");
        }
        
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        
        if (!username.equals(currentUser.getUsername()) && userDAO.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        if (!email.equals(currentUser.getEmail()) && userDAO.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }
        
        currentUser.setUsername(username);
        currentUser.setEmail(email);
        currentUser.setUpdatedAt(LocalDateTime.now());
        
        return userDAO.update(currentUser);
    }
    
    public void changePassword(String currentPassword, String newPassword) throws IllegalArgumentException {
        if (currentUser == null) {
            throw new IllegalStateException("No user is currently logged in");
        }
        
        if (!PasswordHasher.verifyPassword(currentPassword, currentUser.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("New password must be at least 6 characters long");
        }
        
        currentUser.setPasswordHash(PasswordHasher.hashPassword(newPassword));
        currentUser.setUpdatedAt(LocalDateTime.now());
        userDAO.update(currentUser);
    }
    
    public void deleteAccount(String password) throws IllegalArgumentException {
        if (currentUser == null) {
            throw new IllegalStateException("No user is currently logged in");
        }
        
        if (!PasswordHasher.verifyPassword(password, currentUser.getPasswordHash())) {
            throw new IllegalArgumentException("Password is incorrect");
        }
        
        userDAO.delete(currentUser.getId());
        logout();
    }
    
    private void validateSignupInput(String username, String email, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        
        if (username.length() < 3 || username.length() > 30) {
            throw new IllegalArgumentException("Username must be between 3 and 30 characters");
        }
        
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("Username can only contain letters, numbers, and underscores");
        }
        
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
    }
}
