package com.vinatour.backend.controller;

import com.vinatour.backend.dto.request.ChangePasswordRequestDTO;
import com.vinatour.backend.dto.request.ForgotPasswordRequestDTO;
import com.vinatour.backend.dto.request.LoginRequestDTO;
import com.vinatour.backend.dto.request.RegisterRequestDTO;
import com.vinatour.backend.dto.request.ResetPasswordRequestDTO;
import com.vinatour.backend.entity.User;
import com.vinatour.backend.repository.UserRepository;
import com.vinatour.backend.Service.AuthService;
import com.vinatour.backend.util.JwtUtil;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            
            String token = jwtUtil.generateToken(request.getUsername());
            return ResponseEntity.ok(token);
        } catch (org.springframework.security.authentication.DisabledException e) {
            return ResponseEntity.badRequest().body("Tài khoản của bạn đã bị vô hiệu hóa!");
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            return ResponseEntity.badRequest().body("Tài khoản hoặc mật khẩu không chính xác!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Đăng nhập thất bại: " + e.getMessage());
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestBody ChangePasswordRequestDTO request,
            Principal principal) {
        String username = principal.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Mật khẩu hiện tại không chính xác!");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("Tuyệt vời! Đổi mật khẩu thành công!");
    }

    @PostMapping("/register-otp")
    public ResponseEntity<?> sendRegisterOtp(@RequestBody ForgotPasswordRequestDTO request) {
        try {
            return ResponseEntity.ok(authService.sendRegistrationOtp(request.getEmail()));

        } catch (MessagingException e) {

            return ResponseEntity.badRequest().body("Không thể gửi email OTP!");

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO request) {
        try {
            return ResponseEntity.ok(authService.register(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequestDTO request) {
        try {

            return ResponseEntity.ok(authService.generateAndSendOtp(request.getEmail()));

        } catch (MessagingException e) {

            return ResponseEntity.badRequest().body("Không thể gửi email OTP!");

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequestDTO request) {
        try {
            return ResponseEntity.ok(authService.resetPassword(
                    request.getEmail(),
                    request.getOtp(),
                    request.getNewPassword()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}