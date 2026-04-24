package com.snippetmanager.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {
    
    private static final int BCRYPT_WORK_FACTOR = 12;
    
    private PasswordHasher() {
    }
    
    public static String hashPassword(String plainTextPassword) {
        if (plainTextPassword == null || plainTextPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        try {
            String salt = BCrypt.gensalt(BCRYPT_WORK_FACTOR);
            return BCrypt.hashpw(plainTextPassword, salt);
        } catch (Exception e) {
            System.err.println("Error hashing password: " + e.getMessage());
            throw new RuntimeException("Failed to hash password", e);
        }
    }
    
    public static boolean verifyPassword(String plainTextPassword, String hashedPassword) {
        if (plainTextPassword == null || plainTextPassword.isEmpty()) {
            System.out.println("DEBUG: Password is null or empty");
            return false;
        }
        
        if (hashedPassword == null || hashedPassword.isEmpty()) {
            System.out.println("DEBUG: Hash is null or empty");
            return false;
        }
        
        try {
            System.out.println("DEBUG: Verifying password, hash length: " + hashedPassword.length());
            boolean result = BCrypt.checkpw(plainTextPassword, hashedPassword);
            System.out.println("DEBUG: Verification result: " + result);
            return result;
        } catch (Exception e) {
            System.err.println("Error verifying password: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
