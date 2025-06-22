package com.microserviceone.users.termsAndConditionsApi.infraestructure.persistance.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "accepted_terms")
public class AcceptedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "fecha_hora_aceptacion", nullable = false)
    private LocalDateTime fechaHoraDeAceptacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id", nullable = false)
    private TermAndConditionEntity terminoAceptado;

    @Column(nullable = false)
    private String ip;

    // Constructors
    public AcceptedEntity() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public LocalDateTime getFechaHoraDeAceptacion() { return fechaHoraDeAceptacion; }
    public void setFechaHoraDeAceptacion(LocalDateTime fechaHoraDeAceptacion) { 
        this.fechaHoraDeAceptacion = fechaHoraDeAceptacion; 
    }

    public TermAndConditionEntity getTerminoAceptado() { return terminoAceptado; }
    public void setTerminoAceptado(TermAndConditionEntity terminoAceptado) { 
        this.terminoAceptado = terminoAceptado; 
    }

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
} 