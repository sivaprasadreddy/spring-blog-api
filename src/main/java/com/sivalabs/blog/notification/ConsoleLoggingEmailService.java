package com.sivalabs.blog.notification;

import com.sivalabs.blog.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ConditionalOnProperty(name = "blog.email-service-type", havingValue = "console")
public class ConsoleLoggingEmailService implements EmailService {
    private static final Logger LOG = LoggerFactory.getLogger(ConsoleLoggingEmailService.class);
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
        LOG.info(email);
    }
}
