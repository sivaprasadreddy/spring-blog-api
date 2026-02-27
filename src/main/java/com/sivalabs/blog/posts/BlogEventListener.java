package com.sivalabs.blog.posts;

import com.sivalabs.blog.notification.EmailService;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
class BlogEventListener {
    private final EmailService emailService;

    BlogEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @ApplicationModuleListener
    void handle(PostPublishedEvent event) {
        String subject = "New Post Published: " + event.title();
        String content = """
                New Post Published: <a href="%s">%s</a>
                %s
                """.formatted(event.slug(), event.title(), event.content());
        emailService.send(subject, content);
    }
}
