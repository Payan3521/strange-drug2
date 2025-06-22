package com.microserviceone.users.termsAndConditionsApi.web.dto;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TermResponse {
    private Long id;
    private String title;
    private String content;
    private String version;
    private LocalDateTime createTerm;
    private boolean active;
    private String type;

    public TermResponse(Long id, String title, String content, String version, 
                       LocalDateTime createTerm, boolean active, String type) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.version = version;
        this.createTerm = createTerm;
        this.active = active;
        this.type = type;
    }
}