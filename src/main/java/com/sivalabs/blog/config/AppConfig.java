package com.sivalabs.blog.config;

import com.sivalabs.blog.ApplicationProperties;
import com.sivalabs.blog.notification.ConsoleLoggingEmailService;
import com.sivalabs.blog.notification.EmailService;
import com.sivalabs.blog.notification.JavaMailService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;

//@Configuration
class AppConfig {

    @Bean
    @ConditionalOnProperty(name = "blog.email-service-type", havingValue = "console")
    EmailService consoleLoggingEmailService(ApplicationProperties props) {
        return new ConsoleLoggingEmailService(props);
    }

    @Bean
    @ConditionalOnProperty(name = "blog.email-service-type", havingValue = "javamail", matchIfMissing = true)
    EmailService javaMailService(JavaMailSender javaMailSender, ApplicationProperties props) {
        return new JavaMailService(javaMailSender, props);
    }
}
