package com.microserviceone.users.verificationCodeApi.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.microserviceone.users.registrationApi.web.dto.ApiResponse;
import com.microserviceone.users.verificationCodeApi.application.service.VerificationService;
import com.microserviceone.users.verificationCodeApi.web.dto.SendCodeRequest;
import com.microserviceone.users.verificationCodeApi.web.dto.VerifyCodeRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.microserviceone.users.core.rateLimiting.RateLimit;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/verification")
@RestController
@RequiredArgsConstructor
@Tag(name = "Verification", description = "API para la verificación de usuarios")
public class VerificationCodeController {
    private final VerificationService verificationService;

    @Operation(summary = "Enviar código de verificación", description = "Envía un código de verificación al correo electrónico del usuario")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Código de verificación enviado exitosamente",
            content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = Void.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida: correo electrónico no proporcionado o formato incorrecto"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })     
    @RateLimit(key = "verification_send_code", maxRequests = 1, description = "Enviar código de verificación - 1 solicitud cada 5 minutos")
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Void>> sendVerificationCode(@Valid @RequestBody SendCodeRequest request) {
        verificationService.sendCode(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Verification code sent successfully"));
    }

    @Operation(summary = "Verificar código de verificación", description = "Verifica el código de verificación enviado al correo electrónico del usuario")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Código de verificación verificado exitosamente",
            content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = Boolean.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Código inválido o expirado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @RateLimit(key = "verification_verify_code", maxRequests = 1, description = "Verificar código - 1 solicitud cada 5 minutos")
    @PostMapping("/check")
    public ResponseEntity<ApiResponse<Boolean>> verifyCode(@Valid @RequestBody VerifyCodeRequest request){
        boolean isValid = verificationService.verifyCode(request.getEmail(), request.getCode());
        if (isValid) {
            return ResponseEntity.ok(ApiResponse.success("Code verified successfully", true));
        } else {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid or expired verification code"));
        }
    }

    @Operation(summary = "Verificar estado de email", description = "Verifica si el correo electrónico ha sido verificado")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "El email ha sido verificado exitosamente",
            content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = Void.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Correo electronico no proporcionado o formato incorrecto"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno en el servidor")
    })
    @RateLimit(key = "verification_check_status", maxRequests = 5, description = "Verificar estado de email - 5 solicitudes por minuto")
    @PostMapping("/status")
    public ResponseEntity<ApiResponse<Void>> checkEmailVerification(@Valid @RequestBody SendCodeRequest request) {
        verificationService.checkEmailVerification(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Email has been verified"));
    }
}