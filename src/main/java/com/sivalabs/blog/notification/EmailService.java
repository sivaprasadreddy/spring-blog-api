package com.sivalabs.blog.notification;

import com.sivalabs.blog.ApplicationProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender javaMailSender;
    private final ApplicationProperties properties;

    public EmailService(JavaMailSender javaMailSender, ApplicationProperties properties) {
        this.javaMailSender = javaMailSender;
        this.properties = properties;
    }

    @Async
    public void send(String subject, String content) {
        String supportEmail = properties.supportEmail();
        this.send(subject, List.of(supportEmail), content);
    }

    @Async
    public void send(String subject, List<String> to, String content) {
        String supportEmail = properties.supportEmail();
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setTo(to.toArray(new String[0]));
            helper.setReplyTo(supportEmail);
            helper.setFrom(supportEmail);
            helper.setSubject(subject);
            helper.setText(content, true);

            javaMailSender.send(message);
        } catch (MailException | MessagingException e) {
            log.error(e.getMessage(), e);
        }
    }
}
