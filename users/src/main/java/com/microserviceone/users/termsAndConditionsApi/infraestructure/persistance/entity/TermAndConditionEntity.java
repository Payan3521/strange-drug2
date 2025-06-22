package com.microserviceone.users.termsAndConditionsApi.infraestructure.persistance.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "terms_and_conditions")
public class TermAndConditionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String version;

    @Column(name = "create_term", nullable = false)
    private LocalDateTime createTerm;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private String type;

    // Constructors
    public TermAndConditionEntity() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public LocalDateTime getCreateTerm() { return createTerm; }
    public void setCreateTerm(LocalDateTime createTerm) { this.createTerm = createTerm; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
} 
