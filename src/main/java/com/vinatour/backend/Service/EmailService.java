package com.vinatour.backend.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(toEmail);
        helper.setSubject("🔐 Mã OTP xác thực - VinaTour");

        String htmlContent = """
                <div style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 30px;">
                    <div style="max-width: 600px; margin: auto; background: white; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 10px rgba(0,0,0,0.1);">

                        <div style="background: #0ea5e9; padding: 20px; text-align: center;">
                            <h1 style="color: white; margin: 0;">VinaTour</h1>
                        </div>

                        <div style="padding: 30px;">
                            <h2 style="color: #333;">Xin chào 👋</h2>

                            <p style="font-size: 16px; color: #555;">
                                Bạn vừa yêu cầu xác thực tài khoản trên hệ thống <b>VinaTour</b>.
                            </p>

                            <p style="font-size: 16px; color: #555;">
                                Mã OTP của bạn là:
                            </p>

                            <div style="text-align: center; margin: 30px 0;">
                                <span style="
                                    display: inline-block;
                                    background: #0ea5e9;
                                    color: white;
                                    padding: 15px 30px;
                                    font-size: 32px;
                                    letter-spacing: 8px;
                                    border-radius: 10px;
                                    font-weight: bold;">
                                    %s
                                </span>
                            </div>

                            <p style="font-size: 15px; color: #666;">
                                ⏳ Mã này sẽ hết hạn sau <b>5 phút</b>.
                            </p>

                            <p style="font-size: 15px; color: #666;">
                                Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email.
                            </p>

                            <hr style="margin: 30px 0; border: none; border-top: 1px solid #eee;">

                            <p style="font-size: 14px; color: #999; text-align: center;">
                                Trân trọng,<br>
                                Đội ngũ VinaTour
                            </p>
                        </div>
                    </div>
                </div>
                """
                .formatted(otp);

        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}