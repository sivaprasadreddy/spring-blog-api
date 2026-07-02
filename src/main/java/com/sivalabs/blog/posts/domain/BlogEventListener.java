package com.sivalabs.blog.posts.domain;

import com.sivalabs.blog.ApplicationProperties;
import com.sivalabs.blog.notification.EmailService;
import com.sivalabs.blog.posts.domain.models.PostPublishedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
class BlogEventListener {
    private static final Logger LOG = LoggerFactory.getLogger(BlogEventListener.class);
    private final EmailService emailService;
    private final ApplicationProperties properties;

    BlogEventListener(EmailService emailService, ApplicationProperties properties) {
        this.emailService = emailService;
        this.properties = properties;
    }

    @ApplicationModuleListener
    void handle(PostPublishedEvent event) {
        LOG.info("Received PostPublishedEvent with post title: {}", event.title());
        String subject = "New Post Published: " + event.title();
        String content = """
                New Post Published: <a href="%s">%s</a>
                %s
                """.formatted(event.slug(), event.title(), event.content());
        emailService.send(subject, properties.supportEmail(), content);
    }
}
