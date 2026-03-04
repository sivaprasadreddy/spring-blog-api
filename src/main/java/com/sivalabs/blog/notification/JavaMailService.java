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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ConditionalOnProperty(name = "blog.email-service-type", havingValue = "javamail", matchIfMissing = true)
public class JavaMailService implements EmailService {
    private static final Logger LOG = LoggerFactory.getLogger(JavaMailService.class);
    private final JavaMailSender javaMailSender;
    private final ApplicationProperties properties;

    public JavaMailService(JavaMailSender javaMailSender, ApplicationProperties properties) {
        this.javaMailSender = javaMailSender;
        this.properties = properties;
    }

    @Async
    public void send(String to, String subject, String content) {
        this.send(List.of(to), subject, content);
    }

    @Async
    public void send(List<String> to, String subject, String content) {
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
            LOG.error(e.getMessage(), e);
        }
    }
}
