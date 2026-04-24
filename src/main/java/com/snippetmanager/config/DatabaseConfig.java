package com.snippetmanager.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class DatabaseConfig {
    
    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "SnippetDB";
    private static final int CONNECTION_TIMEOUT_MS = 5000;
    
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    
    private DatabaseConfig() {
    }
    
    public static synchronized MongoDatabase getDatabase() {
        if (database == null) {
            try {
                mongoClient = MongoClients.create(CONNECTION_STRING);
                database = mongoClient.getDatabase(DATABASE_NAME);
                System.out.println("Successfully connected to MongoDB database: " + DATABASE_NAME);
                
                database.runCommand(new org.bson.Document("ping", 1));
                System.out.println("Database connection verified successfully");
            } catch (Exception e) {
                System.err.println("Failed to connect to MongoDB: " + e.getMessage());
                throw new RuntimeException("Database connection failed", e);
            }
        }
        return database;
    }
    
    public static void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
            database = null;
            System.out.println("MongoDB connection closed");
        }
    }
    
    public static boolean testConnection() {
        try {
            MongoClient client = MongoClients.create(CONNECTION_STRING);
            client.getDatabase(DATABASE_NAME).runCommand(new org.bson.Document("ping", 1));
            client.close();
            return true;
        } catch (Exception e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }
}
