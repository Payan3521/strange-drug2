package com.microserviceone.users.termsAndConditionsApi.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;

@Getter
public class Accepted {
    
    private final Long id;
    private final User user;
    private final LocalDateTime fechaHoraDeAceptacion;
    private final TermAndCondition terminoAceptado;
    private final String ip;

    public Accepted(User user, TermAndCondition terminoAceptado, String ip) {
        this(null, user, LocalDateTime.now(), terminoAceptado, ip);
    }

    public Accepted(Long id, User user, LocalDateTime fechaHoraDeAceptacion, 
                   TermAndCondition terminoAceptado, String ip) {
        this.id = id;
        this.user = Objects.requireNonNull(user, "User cannot be null");
        this.fechaHoraDeAceptacion = Objects.requireNonNull(fechaHoraDeAceptacion, "Acceptance date cannot be null");
        this.terminoAceptado = Objects.requireNonNull(terminoAceptado, "Term cannot be null");
        this.ip = Objects.requireNonNull(ip, "IP address cannot be null");
        
        validateIpAddress(ip);
        validateTermIsActive(terminoAceptado);
    }


    public boolean isValid() {
        return terminoAceptado.isActive() && 
               fechaHoraDeAceptacion.isBefore(LocalDateTime.now().plusSeconds(1)) &&
               isValidIpAddress(ip);
    }

    private void validateIpAddress(String ip) {
        if (ip == null || ip.trim().isEmpty()) {
            throw new IllegalArgumentException("IP address cannot be null or empty");
        }
        if (!isValidIpAddress(ip)) {
            throw new IllegalArgumentException("Invalid IP address format");
        }
    }

    private void validateTermIsActive(TermAndCondition term) {
        if (!term.isActive()) {
            throw new IllegalArgumentException("Cannot accept inactive terms");
        }
    }

    private boolean isValidIpAddress(String ip) {
        // Basic IP validation - in production, use more robust validation
        return ip.matches("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$") || 
               ip.matches("^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$");
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Accepted accepted = (Accepted) o;
        return Objects.equals(user, accepted.user) && 
               Objects.equals(terminoAceptado, accepted.terminoAceptado);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, terminoAceptado);
    }

    @Override
    public String toString() {
        return "Accepted{" +
                "id=" + id +
                ", user=" + user +
                ", fechaHoraDeAceptacion=" + fechaHoraDeAceptacion +
                ", terminoAceptado=" + terminoAceptado.getVersion() +
                ", ip='" + ip + '\'' +
                '}';
    }
} 