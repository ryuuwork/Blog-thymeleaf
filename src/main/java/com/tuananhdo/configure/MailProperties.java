package com.tuananhdo.configure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
public class MailProperties {

    @Value("${email.sender}")
    private String sender;

    @Value("${email.sender.name}")
    private String senderName;

    @Value("${email.subject}")
    private String subject;

    @Value("${email.content}")
    private String content;

    @Value("${subject.verification.register}")
    private String subjectVerificationRegister;

    @Value("${content.verification.register}")
    private String contentVerificationRegister;

}
