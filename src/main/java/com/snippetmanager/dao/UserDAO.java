package com.snippetmanager.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Updates;
import com.snippetmanager.config.DatabaseConfig;
import com.snippetmanager.model.User;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO {
    
    private static final String COLLECTION_NAME = "users";
    private final MongoCollection<Document> collection;
    
    public UserDAO() {
        MongoDatabase database = DatabaseConfig.getDatabase();
        this.collection = database.getCollection(COLLECTION_NAME);
        createIndexes();
    }
    
    private void createIndexes() {
        try {
            collection.createIndex(Indexes.ascending("username"), 
                new IndexOptions().unique(true));
            collection.createIndex(Indexes.ascending("email"), 
                new IndexOptions().unique(true));
            System.out.println("User collection indexes created successfully");
        } catch (Exception e) {
            System.out.println("Some indexes may already exist: " + e.getMessage());
        }
    }
    
    public User save(User user) {
        try {
            Document doc = toDocument(user);
            doc.put("createdAt", user.getCreatedAt() != null ? user.getCreatedAt() : LocalDateTime.now());
            doc.put("updatedAt", LocalDateTime.now());
            
            collection.insertOne(doc);
            user.setId(doc.getObjectId("_id"));
            System.out.println("User saved successfully: " + user.getUsername());
            return user;
        } catch (Exception e) {
            System.err.println("Error saving user: " + e.getMessage());
            throw new RuntimeException("Failed to save user", e);
        }
    }
    
    public Optional<User> findById(ObjectId id) {
        try {
            Document doc = collection.find(Filters.eq("_id", id)).first();
            return Optional.ofNullable(doc).map(this::fromDocument);
        } catch (Exception e) {
            System.err.println("Error finding user by id: " + e.getMessage());
            return Optional.empty();
        }
    }
    
    public Optional<User> findByUsername(String username) {
        try {
            Document doc = collection.find(Filters.eq("username", username)).first();
            return Optional.ofNullable(doc).map(this::fromDocument);
        } catch (Exception e) {
            System.err.println("Error finding user by username: " + e.getMessage());
            return Optional.empty();
        }
    }
    
    public Optional<User> findByEmail(String email) {
        try {
            Document doc = collection.find(Filters.eq("email", email)).first();
            return Optional.ofNullable(doc).map(this::fromDocument);
        } catch (Exception e) {
            System.err.println("Error finding user by email: " + e.getMessage());
            return Optional.empty();
        }
    }
    
    public List<User> findAll() {
        try {
            List<User> users = new ArrayList<>();
            for (Document doc : collection.find()) {
                users.add(fromDocument(doc));
            }
            return users;
        } catch (Exception e) {
            System.err.println("Error finding all users: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public boolean existsByUsername(String username) {
        try {
            return collection.countDocuments(Filters.eq("username", username)) > 0;
        } catch (Exception e) {
            System.err.println("Error checking username existence: " + e.getMessage());
            return false;
        }
    }
    
    public boolean existsByEmail(String email) {
        try {
            return collection.countDocuments(Filters.eq("email", email)) > 0;
        } catch (Exception e) {
            System.err.println("Error checking email existence: " + e.getMessage());
            return false;
        }
    }
    
    public User update(User user) {
        try {
            collection.updateOne(
                Filters.eq("_id", user.getId()),
                Updates.combine(
                    Updates.set("username", user.getUsername()),
                    Updates.set("email", user.getEmail()),
                    Updates.set("passwordHash", user.getPasswordHash()),
                    Updates.set("updatedAt", LocalDateTime.now())
                )
            );
            System.out.println("User updated successfully: " + user.getUsername());
            return user;
        } catch (Exception e) {
            System.err.println("Error updating user: " + e.getMessage());
            throw new RuntimeException("Failed to update user", e);
        }
    }
    
    public void delete(ObjectId id) {
        try {
            collection.deleteOne(Filters.eq("_id", id));
            System.out.println("User deleted successfully: " + id);
        } catch (Exception e) {
            System.err.println("Error deleting user: " + e.getMessage());
            throw new RuntimeException("Failed to delete user", e);
        }
    }
    
    private Document toDocument(User user) {
        Document doc = new Document();
        if (user.getId() != null) {
            doc.put("_id", user.getId());
        }
        doc.put("username", user.getUsername());
        doc.put("email", user.getEmail());
        doc.put("passwordHash", user.getPasswordHash());
        if (user.getCreatedAt() != null) {
            doc.put("createdAt", user.getCreatedAt().toString());
        }
        if (user.getUpdatedAt() != null) {
            doc.put("updatedAt", user.getUpdatedAt().toString());
        }
        return doc;
    }
    
    private User fromDocument(Document doc) {
        return User.builder()
                .id(doc.getObjectId("_id"))
                .username(doc.getString("username"))
                .email(doc.getString("email"))
                .passwordHash(doc.getString("passwordHash"))
                .createdAt(convertToLocalDateTime(doc.get("createdAt")))
                .updatedAt(convertToLocalDateTime(doc.get("updatedAt")))
                .build();
    }
    
    private LocalDateTime convertToLocalDateTime(Object dateObj) {
        if (dateObj == null) {
            return null;
        }
        try {
            if (dateObj instanceof java.util.Date) {
                return ((java.util.Date) dateObj).toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDateTime();
            } else if (dateObj instanceof String) {
                return LocalDateTime.parse((String) dateObj);
            } else if (dateObj instanceof java.time.Instant) {
                return ((java.time.Instant) dateObj)
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDateTime();
            }
        } catch (Exception e) {
            System.out.println("Failed to convert datetime: " + dateObj + " (type: " + dateObj.getClass().getName() + ")");
        }
        return null;
    }
}
