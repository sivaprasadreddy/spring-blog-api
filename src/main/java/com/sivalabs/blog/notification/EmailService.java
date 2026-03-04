package com.sivalabs.blog.notification;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EmailService {

    void send(String to, String subject, String content);

    void send(List<String> to, String subject, String content);

}
