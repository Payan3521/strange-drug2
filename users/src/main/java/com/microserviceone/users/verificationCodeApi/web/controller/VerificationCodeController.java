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
import com.microserviceone.users.core.rateLimiting.RateLimit;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/verification")
@RestController
@RequiredArgsConstructor
public class VerificationCodeController {
    private final VerificationService verificationService;

    @RateLimit(key = "verification_send_code", maxRequests = 1, description = "Enviar código de verificación - 1 solicitud cada 5 minutos")
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Void>> sendVerificationCode(@Valid @RequestBody SendCodeRequest request) {
        verificationService.sendCode(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Verification code sent successfully"));
    }

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

    @RateLimit(key = "verification_check_status", maxRequests = 5, description = "Verificar estado de email - 5 solicitudes por minuto")
    @PostMapping("/status")
    public ResponseEntity<ApiResponse<Void>> checkEmailVerification(@Valid @RequestBody SendCodeRequest request) {
        verificationService.checkEmailVerification(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Email has been verified"));
    }
}