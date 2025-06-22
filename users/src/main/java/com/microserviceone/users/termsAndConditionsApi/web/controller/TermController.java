package com.microserviceone.users.termsAndConditionsApi.web.controller;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.microserviceone.users.termsAndConditionsApi.application.service.TermsService;
import com.microserviceone.users.termsAndConditionsApi.domain.model.Accepted;
import com.microserviceone.users.termsAndConditionsApi.web.dto.AcceptedMultipleRequest;
import com.microserviceone.users.termsAndConditionsApi.web.dto.AcceptedRequest;
import com.microserviceone.users.termsAndConditionsApi.web.dto.ApiResponse;
import com.microserviceone.users.termsAndConditionsApi.web.dto.TermResponse;
import com.microserviceone.users.termsAndConditionsApi.web.webMapper.TermWebMapper;
import com.microserviceone.users.core.rateLimiting.RateLimit;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/terms")
@RequiredArgsConstructor
public class TermController {
    
    private final TermsService termsService;
    private final TermWebMapper termWebMapper;

    @RateLimit(key = "terms_get_all_active", maxRequests = 20, description = "Obtener términos activos - 20 solicitudes por minuto")
    @GetMapping
    public ResponseEntity<ApiResponse<List<TermResponse>>> getAllActiveTerms() {
        List<TermResponse> terms = termsService.getAllActiveTerms().stream()
            .map(termWebMapper::toResponse)
            .collect(Collectors.toList());

        if(!terms.isEmpty()){
            return ResponseEntity.ok(ApiResponse.success("Active terms retrieved successfully", terms));
        }
        return ResponseEntity.noContent().build();
       
    }

    @RateLimit(key = "terms_get_by_type", maxRequests = 20, description = "Obtener término por tipo - 20 solicitudes por minuto")
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<TermResponse>> getActiveTermByType(@PathVariable String type) {
        return termsService.getActiveTermByType(type)
        .map(term -> ResponseEntity.ok(ApiResponse.success("Term retrieved successfully", termWebMapper.toResponse(term))))
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error("No active term found for type: " + type)));
    }

    @RateLimit(key = "terms_accept_single", maxRequests = 3, description = "Aceptar término individual - 3 solicitudes por minuto")
    @PostMapping("/accept")
    public ResponseEntity<ApiResponse<Accepted>> acceptTerm(
            @Valid @RequestBody AcceptedRequest request,
            HttpServletRequest httpRequest) {
        
        String ipAddress = httpRequest.getRemoteAddr();
        Accepted accepted = termsService.acceptTerm(request.getUserId(), request.getUserEmail(), request.getTermId(), ipAddress);
        
        return ResponseEntity.ok(ApiResponse.success("Terms accepted successfully", accepted));
    }

    @RateLimit(key = "terms_accept_multiple", maxRequests = 3, description = "Aceptar múltiples términos - 3 solicitudes por minuto")
    @PostMapping("/accept/multiple")
    public ResponseEntity<ApiResponse<List<Accepted>>> acceptMultipleTerms(
            @Valid @RequestBody AcceptedMultipleRequest request,
            HttpServletRequest httpRequest) {

        String ipAddress = httpRequest.getRemoteAddr();
        List<Accepted> acceptedList = termsService.acceptAll(
            request.getUserId(), request.getUserEmail(), request.getTermIds(), ipAddress);

        return ResponseEntity.ok(ApiResponse.success("Términos aceptados correctamente", acceptedList));
    }

    @RateLimit(key = "terms_verify_acceptance", maxRequests = 5, description = "Verificar aceptación de término - 5 solicitudes por minuto")
    @GetMapping("/verify/{userId}/{termId}")
    public ResponseEntity<ApiResponse<Boolean>> verifyAcceptance(
            @PathVariable Long userId,
            @PathVariable Long termId) {
        
        boolean hasAccepted = termsService.hasAcceptedTerm(userId, termId);
        return ResponseEntity.ok(ApiResponse.success("Verification completed", hasAccepted));
    }

    @RateLimit(key = "terms_verify_by_email", maxRequests = 5, description = "Verificar todos los términos por email - 5 solicitudes por minuto")
    @GetMapping("/verify/email/{email}")
    public ResponseEntity<ApiResponse<Void>> verifyAllTermsByEmail(@PathVariable String email) {
        termsService.verifyAllTermsAccepted(email);
        return ResponseEntity.ok(ApiResponse.success("Todos los términos han sido aceptados"));
    }  

    @RateLimit(key = "terms_get_all", maxRequests = 20, description = "Obtener todos los términos - 20 solicitudes por minuto")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<TermResponse>>> getAllTerms() {
        List<TermResponse> terms = termsService.getAllTerms().stream()
            .map(termWebMapper::toResponse)
            .collect(Collectors.toList());

        if(!terms.isEmpty()){
            return ResponseEntity.ok(ApiResponse.success("All terms retrieved successfully", terms));
        }
        return ResponseEntity.noContent().build();
    }

    @RateLimit(key = "terms_get_by_id", maxRequests = 20, description = "Obtener término por ID - 20 solicitudes por minuto")
    @GetMapping("/id/{id}")
    public ResponseEntity<TermResponse> getTermById(@PathVariable Long id) {
        return termsService.getById(id)
            .map(term -> new ResponseEntity<>(termWebMapper.toResponse(term), HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}