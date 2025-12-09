package learning.auth.services;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void sendResetLink(String toEmail, String code){
        String subject = "Mã xác thực đặt lại mật khẩu - SmartCode";

        String htmlContent = String.format("""
            <div style="font-family: Helvetica, Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #eee; border-radius: 10px;">
                <h2 style="color: #333; text-align: center;">Yêu cầu đặt lại mật khẩu</h2>
                <p style="font-size: 16px; color: #555;">Xin chào,</p>
                <p style="font-size: 16px; color: #555;">Dưới đây là mã xác thực (OTP) để đặt lại mật khẩu của bạn. Mã này sẽ hết hạn sau <strong>15 phút</strong>.</p>
                
                <div style="text-align: center; margin: 30px 0;">
                    <span style="font-size: 32px; font-weight: bold; letter-spacing: 5px; color: #4CAF50; background: #f4f4f4; padding: 15px 30px; border-radius: 5px; border: 1px dashed #4CAF50;">
                        %s
                    </span>
                </div>
                
                <p style="font-size: 14px; color: #999; text-align: center;">Nếu bạn không yêu cầu, vui lòng bỏ qua email này. Tuyệt đối không chia sẻ mã này cho ai khác.</p>
            </div>
            """, code);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send OTP to {}", toEmail, e);
        }
    }
}
