package com.vinatour.backend.Service;

import com.vinatour.backend.dto.request.RegisterRequestDTO;
import com.vinatour.backend.entity.User;
import com.vinatour.backend.repository.UserRepository;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;

    public String generateAndSendOtp(String email) throws MessagingException {
        if (!userRepository.existsByEmail(email)) {
            throw new RuntimeException("Tài khoản với email này không tồn tại!");
        }

        String otp = String.format("%06d", new Random().nextInt(999999));

        otpService.saveOtp(email, otp);

        emailService.sendOtpEmail(email, otp);

        return "Đã gửi mã OTP đặt lại mật khẩu đến email của bạn!";
    }

    public String resetPassword(String email, String otp, String newPassword) {
        String cachedOtp = otpService.getOtp(email);

        if (cachedOtp == null) {
            throw new RuntimeException("Mã OTP đã hết hạn hoặc không tồn tại!");
        }
        if (!cachedOtp.equals(otp)) {
            throw new RuntimeException("Mã OTP không chính xác!");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Lỗi hệ thống: Không tìm thấy User!"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        otpService.clearOtp(email);

        return "Đổi mật khẩu thành công!";
    }

    public String sendRegistrationOtp(String email) throws MessagingException {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email này đã được đăng ký rồi!");
        }

        String otp = String.format("%06d", new Random().nextInt(999999));

        otpService.saveOtp("REG_" + email, otp);

        emailService.sendOtpEmail(email, otp);

        return "Mã xác thực đăng ký đã được gửi đến email của bạn!";
    }

    public String register(RegisterRequestDTO request) {
        String cachedOtp = otpService.getOtp("REG_" + request.getEmail());

        if (cachedOtp == null) {
            throw new RuntimeException("Mã OTP đã hết hạn!");
        }
        if (!cachedOtp.equals(request.getOtp())) {
            throw new RuntimeException("Mã OTP không chính xác!");
        }

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username này đã có người sử dụng!");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        otpService.clearOtp("REG_" + request.getEmail());

        return "Chúc mừng! Bạn đã đăng ký tài khoản VinaTour thành công.";
    }
}