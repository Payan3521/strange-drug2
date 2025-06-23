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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.microserviceone.users.core.rateLimiting.RateLimit;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/terms")
@RequiredArgsConstructor
@Tag(name = "Terms and Conditions", description = "API para la gestión de términos y condiciones")
public class TermController {
    
    private final TermsService termsService;
    private final TermWebMapper termWebMapper;

    @Operation(summary = "Obtener términos activos", description = "Obtiene todos los términos y condiciones activos")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Términos activos obtenidos exitosamente",
            content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = TermResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "No se encontraron términos activos"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
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

    @Operation(summary = "Obtener término activo por tipo", description = "Obtiene un término activo por su tipo")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Término activo obtenido exitosamente",
            content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = TermResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No se encontró un término activo para el tipo especificado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @RateLimit(key = "terms_get_by_type", maxRequests = 20, description = "Obtener término por tipo - 20 solicitudes por minuto")
    @GetMapping("/type")
    public ResponseEntity<ApiResponse<TermResponse>> getActiveTermByType(@RequestParam(required = true) String type) {
        return termsService.getActiveTermByType(type)
        .map(term -> ResponseEntity.ok(ApiResponse.success("Term retrieved successfully", termWebMapper.toResponse(term))))
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error("No active term found for type: " + type)));
    }

    @Operation(summary = "Aceptar término individual", description = "Acepta un término individual por ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Término aceptado exitosamente",
            content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = Accepted.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida: ID de término no proporcionado o usuario no encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @RateLimit(key = "terms_accept_single", maxRequests = 3, description = "Aceptar término individual - 3 solicitudes por minuto")
    @PostMapping("/accept")
    public ResponseEntity<ApiResponse<Accepted>> acceptTerm(
            @Valid @RequestBody AcceptedRequest request,
            HttpServletRequest httpRequest) {
        
        String ipAddress = httpRequest.getRemoteAddr();
        Accepted accepted = termsService.acceptTerm(request.getUserId(), request.getUserEmail(), request.getTermId(), ipAddress);
        
        return ResponseEntity.ok(ApiResponse.success("Terms accepted successfully", accepted));
    }

    @Operation(summary = "Aceptar múltiples términos", description = "Acepta múltiples términos por sus IDs")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Términos aceptados exitosamente",
            content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = Accepted.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida: IDs de términos no proporcionados o usuario no encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
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

    @Operation(summary = "Verificar aceptación de término", description = "Verifica si un usuario ha aceptado un término específico")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Verificación de aceptación completada",
            content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = Boolean.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No se encontró el término o el usuario"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @RateLimit(key = "terms_verify_acceptance", maxRequests = 5, description = "Verificar aceptación de término - 5 solicitudes por minuto")
    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<Boolean>> verifyAcceptance(
            @RequestParam(required = true) Long userId,
            @RequestParam(required = true) Long termId) {
        
        boolean hasAccepted = termsService.hasAcceptedTerm(userId, termId);
        return ResponseEntity.ok(ApiResponse.success("Verification completed", hasAccepted));
    }

    @Operation(summary = "Verificar aceptación de todos los términos por email", description = "Verifica si un usuario ha aceptado todos los términos por su email")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Verificación de aceptación de todos los términos completada",
            content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = Void.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No se encontraron términos o el usuario no existe"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @RateLimit(key = "terms_verify_by_email", maxRequests = 5, description = "Verificar todos los términos por email - 5 solicitudes por minuto")
    @GetMapping("/verify/")
    public ResponseEntity<ApiResponse<Void>> verifyAllTermsByEmail(@RequestParam(required = true) String email) {
        termsService.verifyAllTermsAccepted(email);
        return ResponseEntity.ok(ApiResponse.success("Todos los términos han sido aceptados"));
    }  

    @Operation(summary = "Obtener todos los términos", description = "Obtiene todos los términos y condiciones disponibles")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Términos obtenidos exitosamente",
            content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = TermResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "No se encontraron términos"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
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

    @Operation(summary = "Obtener término por ID", description = "Obtiene un término específico por su ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Término obtenido exitosamente",
            content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = TermResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No se encontró el término con el ID especificado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @RateLimit(key = "terms_get_by_id", maxRequests = 20, description = "Obtener término por ID - 20 solicitudes por minuto")
    @GetMapping("/id/{id}")
    public ResponseEntity<TermResponse> getTermById(@PathVariable Long id) {
        return termsService.getById(id)
            .map(term -> new ResponseEntity<>(termWebMapper.toResponse(term), HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}