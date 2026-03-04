package com.sivalabs.blog.jobs;

import com.sivalabs.blog.notification.EmailService;
import com.sivalabs.blog.posts.PostDto;
import com.sivalabs.blog.posts.PostsAPI;
import com.sivalabs.blog.users.UserDto;
import com.sivalabs.blog.users.UsersAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
class WeeklyEmailSenderJob {
    private static final Logger LOG = LoggerFactory.getLogger(WeeklyEmailSenderJob.class);
    private final PostsAPI postsAPI;
    private final UsersAPI usersAPI;
    private final EmailService emailService;

    WeeklyEmailSenderJob(PostsAPI postServiceImpl, UsersAPI usersAPI, EmailService emailService) {
        this.postsAPI = postServiceImpl;
        this.usersAPI = usersAPI;
        this.emailService = emailService;
    }

    @Scheduled(cron = "${blog.newsletter-job-cron}")
    void sendNewsLetter() {
        LOG.info("Sending newsletter at {}", Instant.now());
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime startOfWeek = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
        List<PostDto> posts = postsAPI.findPostsCreatedBetween(startOfWeek, end);
        if (posts.isEmpty()) {
            LOG.info("No posts found for this week. Skipping newsletter");
            return;
        }
        String newsLetterContent = createNewsLetterContent(posts);
        List<String> userEmails =
                usersAPI.findAllUsers().stream().map(UserDto::email).toList();
        if (userEmails.isEmpty()) {
            LOG.info("No users found for this week. Skipping newsletter");
            return;
        }
        emailService.send(userEmails, "Weekly Newsletter", newsLetterContent);
        LOG.info("Sent newsletter at {} to {} users", Instant.now(), userEmails.size());
    }

    private String createNewsLetterContent(List<PostDto> posts) {
        StringBuilder emailContent = new StringBuilder();
        for (PostDto post : posts) {
            // Externalize base url
            String postUrl = "http://localhost:8080/blog/posts/" + post.slug();
            var fragment = """
                    <h2><a href="%s">%s</a></h2>
                    <p>%s</p>
                    """.formatted(postUrl, post.title(), post.content());
            emailContent.append(fragment);
        }
        return emailContent.toString();
    }
}
