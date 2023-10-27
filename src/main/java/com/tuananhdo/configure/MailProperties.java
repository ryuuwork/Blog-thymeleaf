package com.tuananhdo.configure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@PropertySource("classpath:mail.properties")
@ConfigurationProperties(prefix = "email")
public class MailProperties {

    private String sender;
    private String senderName;
    private String subject;
    private String content;
    private String subjectVerificationRegister;
    private String contentVerificationRegister;
}
