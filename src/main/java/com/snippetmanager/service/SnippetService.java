package com.snippetmanager.service;

import com.snippetmanager.dao.SnippetDAO;
import com.snippetmanager.model.Snippet;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SnippetService {
    
    private final SnippetDAO snippetDAO;
    private final UserService userService;
    
    public SnippetService(UserService userService) {
        this.snippetDAO = new SnippetDAO();
        this.userService = userService;
    }
    
    public Snippet createSnippet(String title, String programmingLanguage, String code, 
                                  String tags, String description) throws IllegalArgumentException {
        
        ObjectId userId = userService.getCurrentUserId();
        if (userId == null) {
            throw new IllegalStateException("User must be logged in to create snippets");
        }
        
        validateSnippetInput(title, programmingLanguage, code);
        
        List<String> tagList = parseTags(tags);
        
        Snippet snippet = Snippet.builder()
                .userId(userId)
                .title(title.trim())
                .programmingLanguage(programmingLanguage.trim())
                .code(code)
                .tags(tagList)
                .description(description != null ? description.trim() : "")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        return snippetDAO.save(snippet);
    }
    
    public Snippet updateSnippet(ObjectId snippetId, String title, String programmingLanguage, 
                                  String code, String tags, String description) throws IllegalArgumentException {
        
        ObjectId userId = userService.getCurrentUserId();
        if (userId == null) {
            throw new IllegalStateException("User must be logged in to update snippets");
        }
        
        Optional<Snippet> existingSnippet = snippetDAO.findById(snippetId);
        if (existingSnippet.isEmpty()) {
            throw new IllegalArgumentException("Snippet not found");
        }
        
        Snippet snippet = existingSnippet.get();
        if (!snippet.getUserId().equals(userId)) {
            throw new IllegalStateException("You can only update your own snippets");
        }
        
        validateSnippetInput(title, programmingLanguage, code);
        
        snippet.setTitle(title.trim());
        snippet.setProgrammingLanguage(programmingLanguage.trim());
        snippet.setCode(code);
        snippet.setTags(parseTags(tags));
        snippet.setDescription(description != null ? description.trim() : "");
        snippet.setUpdatedAt(LocalDateTime.now());
        
        return snippetDAO.update(snippet);
    }
    
    public void deleteSnippet(ObjectId snippetId) throws IllegalArgumentException {
        ObjectId userId = userService.getCurrentUserId();
        if (userId == null) {
            throw new IllegalStateException("User must be logged in to delete snippets");
        }
        
        Optional<Snippet> existingSnippet = snippetDAO.findById(snippetId);
        if (existingSnippet.isEmpty()) {
            throw new IllegalArgumentException("Snippet not found");
        }
        
        Snippet snippet = existingSnippet.get();
        if (!snippet.getUserId().equals(userId)) {
            throw new IllegalStateException("You can only delete your own snippets");
        }
        
        snippetDAO.delete(snippetId);
    }
    
    public Optional<Snippet> getSnippetById(ObjectId snippetId) {
        ObjectId userId = userService.getCurrentUserId();
        if (userId == null) {
            return Optional.empty();
        }
        
        Optional<Snippet> snippet = snippetDAO.findById(snippetId);
        if (snippet.isPresent() && snippet.get().getUserId().equals(userId)) {
            return snippet;
        }
        return Optional.empty();
    }
    
    public List<Snippet> getUserSnippets() {
        ObjectId userId = userService.getCurrentUserId();
        if (userId == null) {
            return new ArrayList<>();
        }
        return snippetDAO.findByUserId(userId);
    }
    
    public List<Snippet> searchSnippets(String query, String filterType) {
        ObjectId userId = userService.getCurrentUserId();
        if (userId == null) {
            return new ArrayList<>();
        }
        
        if (query == null || query.trim().isEmpty()) {
            return getUserSnippets();
        }
        
        String searchTerm = query.trim();
        
        return switch (filterType != null ? filterType.toLowerCase() : "all") {
            case "title" -> snippetDAO.searchByTitle(userId, searchTerm);
            case "language" -> snippetDAO.searchByLanguage(userId, searchTerm);
            case "tags" -> snippetDAO.searchByTags(userId, Arrays.asList(searchTerm.split(",")));
            default -> snippetDAO.searchAllFields(userId, searchTerm);
        };
    }
    
    public List<Snippet> filterByLanguage(String language) {
        ObjectId userId = userService.getCurrentUserId();
        if (userId == null) {
            return new ArrayList<>();
        }
        
        if (language == null || language.trim().isEmpty()) {
            return getUserSnippets();
        }
        
        return snippetDAO.searchByLanguage(userId, language.trim());
    }
    
    public List<Snippet> filterByTags(List<String> tags) {
        ObjectId userId = userService.getCurrentUserId();
        if (userId == null) {
            return new ArrayList<>();
        }
        
        if (tags == null || tags.isEmpty()) {
            return getUserSnippets();
        }
        
        return snippetDAO.searchByTags(userId, tags);
    }
    
    public long getSnippetCount() {
        ObjectId userId = userService.getCurrentUserId();
        if (userId == null) {
            return 0;
        }
        return snippetDAO.countByUserId(userId);
    }
    
    public List<String> getAllLanguages() {
        List<Snippet> snippets = getUserSnippets();
        return snippets.stream()
                .map(Snippet::getProgrammingLanguage)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
    
    public List<String> getAllTags() {
        List<Snippet> snippets = getUserSnippets();
        return snippets.stream()
                .flatMap(s -> s.getTags().stream())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
    
    private void validateSnippetInput(String title, String programmingLanguage, String code) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        
        if (title.length() > 100) {
            throw new IllegalArgumentException("Title cannot exceed 100 characters");
        }
        
        if (programmingLanguage == null || programmingLanguage.trim().isEmpty()) {
            throw new IllegalArgumentException("Programming language cannot be empty");
        }
        
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Code cannot be empty");
        }
    }
    
    private List<String> parseTags(String tags) {
        if (tags == null || tags.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }
}
