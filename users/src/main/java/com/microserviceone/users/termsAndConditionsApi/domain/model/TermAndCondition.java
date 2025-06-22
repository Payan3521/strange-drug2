package com.microserviceone.users.termsAndConditionsApi.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TermAndCondition {
    private Long id;
    private String title;
    private String content;
    private String version;
    private LocalDateTime createTerm;
    private boolean active;
    private TermType type;

    public TermAndCondition(String title, String content, String version, TermType type) {
        this(null, title, content, version, LocalDateTime.now(), true, type);
    }

    public TermAndCondition(Long id, String title, String content, String version, 
                           LocalDateTime createTerm, boolean active, TermType type) {
        this.id = id;
        this.title = Objects.requireNonNull(title, "Title cannot be null");
        this.content = Objects.requireNonNull(content, "Content cannot be null");
        this.version = Objects.requireNonNull(version, "Version cannot be null");
        this.createTerm = Objects.requireNonNull(createTerm, "Create date cannot be null");
        this.active = active;
        this.type = Objects.requireNonNull(type, "Type cannot be null");
        
        validateTitle(title);
        validateContent(content);
        validateVersion(version);
    }


    public boolean isActive() {
        return active;
    }

    public TermAndCondition createNewVersion(String newContent, String newVersion) {
        return new TermAndCondition(title, newContent, newVersion, type);
    }

    private void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        if (title.length() > 200) {
            throw new IllegalArgumentException("Title cannot exceed 200 characters");
        }
    }

    private void validateContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be null or empty");
        }
        if (content.length() < 50) {
            throw new IllegalArgumentException("Content must be at least 50 characters");
        }
    }

    private void validateVersion(String version) {
        if (version == null || !version.matches("\\d+\\.\\d+(\\.\\d+)?")) {
            throw new IllegalArgumentException("Version must follow semantic versioning (e.g., 1.0 or 1.0.0)");
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TermAndCondition that = (TermAndCondition) o;
        return Objects.equals(version, that.version) && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, type);
    }

    @Override
    public String toString() {
        return "TermAndCondition{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", version='" + version + '\'' +
                ", type=" + type +
                ", active=" + active +
                ", createTerm=" + createTerm +
                '}';
    }

    public enum TermType {
        GENERAL,
        PRIVACY,
        CREATOR,
        PAYMENT
    }
}
