package com.snippetmanager.model;

import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.List;

public class Snippet {
    private ObjectId id;
    private ObjectId userId;
    private String title;
    private String programmingLanguage;
    private String code;
    private List<String> tags;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Snippet() {
    }

    public Snippet(ObjectId id, ObjectId userId, String title, String programmingLanguage,
                   String code, List<String> tags, String description,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.programmingLanguage = programmingLanguage;
        this.code = code;
        this.tags = tags;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public ObjectId getUserId() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProgrammingLanguage() {
        return programmingLanguage;
    }

    public void setProgrammingLanguage(String programmingLanguage) {
        this.programmingLanguage = programmingLanguage;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ObjectId id;
        private ObjectId userId;
        private String title;
        private String programmingLanguage;
        private String code;
        private List<String> tags;
        private String description;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(ObjectId id) {
            this.id = id;
            return this;
        }

        public Builder userId(ObjectId userId) {
            this.userId = userId;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder programmingLanguage(String programmingLanguage) {
            this.programmingLanguage = programmingLanguage;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder tags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Snippet build() {
            return new Snippet(id, userId, title, programmingLanguage, code, 
                             tags, description, createdAt, updatedAt);
        }
    }
}
