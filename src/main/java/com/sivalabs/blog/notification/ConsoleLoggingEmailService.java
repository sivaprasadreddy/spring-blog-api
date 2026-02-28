package com.sivalabs.blog.notification;

import com.sivalabs.blog.ApplicationProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "blog.email-service-type", havingValue = "console")
public class ConsoleLoggingEmailService implements EmailService {
    private static final Logger log = LoggerFactory.getLogger(ConsoleLoggingEmailService.class);
    private final ApplicationProperties properties;

    public ConsoleLoggingEmailService(ApplicationProperties properties) {
        this.properties = properties;
    }


    @Async
    public void send(String to, String subject, String content) {
        this.send(List.of(to), subject, content);
    }

    @Async
    public void send(List<String> to, String subject, String content) {
        String supportEmail = properties.supportEmail();
        String email = """
                ======================================================
                From: %s
                To: %s
                Subject: %s
                
                %s
                ======================================================
                """.formatted(supportEmail, to, subject, content);
        log.info(email);
    }
}
