package com.snippetmanager.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import com.snippetmanager.config.DatabaseConfig;
import com.snippetmanager.model.Snippet;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SnippetDAO {
    
    private static final String COLLECTION_NAME = "snippets";
    private final MongoCollection<Document> collection;
    
    public SnippetDAO() {
        MongoDatabase database = DatabaseConfig.getDatabase();
        this.collection = database.getCollection(COLLECTION_NAME);
        createIndexes();
    }
    
    private void createIndexes() {
        try {
            collection.createIndex(Indexes.ascending("userId"));
            collection.createIndex(Indexes.text("title"));
            collection.createIndex(Indexes.text("description"));
            collection.createIndex(Indexes.ascending("programmingLanguage"));
            collection.createIndex(Indexes.ascending("tags"));
            System.out.println("Snippet collection indexes created successfully");
        } catch (Exception e) {
            System.out.println("Some indexes may already exist: " + e.getMessage());
        }
    }
    
    public Snippet save(Snippet snippet) {
        try {
            Document doc = toDocument(snippet);
            if (snippet.getCreatedAt() == null) {
                doc.put("createdAt", LocalDateTime.now());
            }
            doc.put("updatedAt", LocalDateTime.now());
            
            collection.insertOne(doc);
            snippet.setId(doc.getObjectId("_id"));
            System.out.println("Snippet saved successfully: " + snippet.getTitle());
            return snippet;
        } catch (Exception e) {
            System.err.println("Error saving snippet: " + e.getMessage());
            throw new RuntimeException("Failed to save snippet", e);
        }
    }
    
    public Optional<Snippet> findById(ObjectId id) {
        try {
            Document doc = collection.find(Filters.eq("_id", id)).first();
            return Optional.ofNullable(doc).map(this::fromDocument);
        } catch (Exception e) {
            System.err.println("Error finding snippet by id: " + e.getMessage());
            return Optional.empty();
        }
    }
    
    public List<Snippet> findByUserId(ObjectId userId) {
        try {
            List<Snippet> snippets = new ArrayList<>();
            for (Document doc : collection.find(Filters.eq("userId", userId))
                    .sort(Sorts.descending("createdAt"))) {
                snippets.add(fromDocument(doc));
            }
            return snippets;
        } catch (Exception e) {
            System.err.println("Error finding snippets by user id: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public List<Snippet> findAll() {
        try {
            List<Snippet> snippets = new ArrayList<>();
            for (Document doc : collection.find().sort(Sorts.descending("createdAt"))) {
                snippets.add(fromDocument(doc));
            }
            return snippets;
        } catch (Exception e) {
            System.err.println("Error finding all snippets: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public List<Snippet> searchByTitle(ObjectId userId, String keyword) {
        try {
            List<Snippet> snippets = new ArrayList<>();
            Pattern pattern = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE);
            
            for (Document doc : collection.find(Filters.and(
                    Filters.eq("userId", userId),
                    Filters.regex("title", pattern)
                )).sort(Sorts.descending("createdAt"))) {
                snippets.add(fromDocument(doc));
            }
            return snippets;
        } catch (Exception e) {
            System.err.println("Error searching snippets by title: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public List<Snippet> searchByLanguage(ObjectId userId, String language) {
        try {
            List<Snippet> snippets = new ArrayList<>();
            for (Document doc : collection.find(Filters.and(
                    Filters.eq("userId", userId),
                    Filters.eq("programmingLanguage", language)
                )).sort(Sorts.descending("createdAt"))) {
                snippets.add(fromDocument(doc));
            }
            return snippets;
        } catch (Exception e) {
            System.err.println("Error searching snippets by language: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public List<Snippet> searchByTags(ObjectId userId, List<String> tags) {
        try {
            List<Snippet> snippets = new ArrayList<>();
            for (Document doc : collection.find(Filters.and(
                    Filters.eq("userId", userId),
                    Filters.in("tags", tags)
                )).sort(Sorts.descending("createdAt"))) {
                snippets.add(fromDocument(doc));
            }
            return snippets;
        } catch (Exception e) {
            System.err.println("Error searching snippets by tags: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public List<Snippet> searchAllFields(ObjectId userId, String keyword) {
        try {
            List<Snippet> snippets = new ArrayList<>();
            Pattern pattern = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE);
            
            for (Document doc : collection.find(Filters.and(
                    Filters.eq("userId", userId),
                    Filters.or(
                        Filters.regex("title", pattern),
                        Filters.regex("description", pattern),
                        Filters.regex("code", pattern),
                        Filters.regex("programmingLanguage", pattern)
                    )
                )).sort(Sorts.descending("createdAt"))) {
                snippets.add(fromDocument(doc));
            }
            return snippets;
        } catch (Exception e) {
            System.err.println("Error searching snippets: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public Snippet update(Snippet snippet) {
        try {
            collection.updateOne(
                Filters.eq("_id", snippet.getId()),
                Updates.combine(
                    Updates.set("title", snippet.getTitle()),
                    Updates.set("programmingLanguage", snippet.getProgrammingLanguage()),
                    Updates.set("code", snippet.getCode()),
                    Updates.set("tags", snippet.getTags()),
                    Updates.set("description", snippet.getDescription()),
                    Updates.set("updatedAt", LocalDateTime.now())
                )
            );
            System.out.println("Snippet updated successfully: " + snippet.getTitle());
            return snippet;
        } catch (Exception e) {
            System.err.println("Error updating snippet: " + e.getMessage());
            throw new RuntimeException("Failed to update snippet", e);
        }
    }
    
    public void delete(ObjectId id) {
        try {
            collection.deleteOne(Filters.eq("_id", id));
            System.out.println("Snippet deleted successfully: " + id);
        } catch (Exception e) {
            System.err.println("Error deleting snippet: " + e.getMessage());
            throw new RuntimeException("Failed to delete snippet", e);
        }
    }
    
    public void deleteByUserId(ObjectId userId) {
        try {
            collection.deleteMany(Filters.eq("userId", userId));
            System.out.println("All snippets deleted for user: " + userId);
        } catch (Exception e) {
            System.err.println("Error deleting user snippets: " + e.getMessage());
            throw new RuntimeException("Failed to delete user snippets", e);
        }
    }
    
    public long countByUserId(ObjectId userId) {
        try {
            return collection.countDocuments(Filters.eq("userId", userId));
        } catch (Exception e) {
            System.err.println("Error counting snippets: " + e.getMessage());
            return 0;
        }
    }
    
    private Document toDocument(Snippet snippet) {
        Document doc = new Document();
        if (snippet.getId() != null) {
            doc.put("_id", snippet.getId());
        }
        doc.put("userId", snippet.getUserId());
        doc.put("title", snippet.getTitle());
        doc.put("programmingLanguage", snippet.getProgrammingLanguage());
        doc.put("code", snippet.getCode());
        doc.put("tags", snippet.getTags());
        doc.put("description", snippet.getDescription());
        if (snippet.getCreatedAt() != null) {
            doc.put("createdAt", snippet.getCreatedAt().toString());
        }
        if (snippet.getUpdatedAt() != null) {
            doc.put("updatedAt", snippet.getUpdatedAt().toString());
        }
        return doc;
    }
    
    private Snippet fromDocument(Document doc) {
        List<String> tags = new ArrayList<>();
        if (doc.get("tags") != null) {
            tags = ((List<?>) doc.get("tags")).stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
        }
        
        return Snippet.builder()
                .id(doc.getObjectId("_id"))
                .userId(doc.getObjectId("userId"))
                .title(doc.getString("title"))
                .programmingLanguage(doc.getString("programmingLanguage"))
                .code(doc.getString("code"))
                .tags(tags)
                .description(doc.getString("description"))
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
