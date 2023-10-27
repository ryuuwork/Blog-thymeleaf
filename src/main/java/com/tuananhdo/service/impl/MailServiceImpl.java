package com.tuananhdo.service.impl;

import com.tuananhdo.configure.MailProperties;
import com.tuananhdo.payload.RegistrationDTO;
import com.tuananhdo.service.MailService;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Service
@AllArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender javaMailSender;
    private final MailProperties mailProperties;

    @Override
    public void sendMailForgotPassword(String email, String resetPasswordToken) {
        sendMail(email, mailProperties.getSubject(), String.format(mailProperties.getContent(), resetPasswordToken));
    }

    @Override
    public void sendVerificationMail(RegistrationDTO registrationDTO, String verifyToken) {
        sendMail(registrationDTO.getEmail(), mailProperties.getSubjectVerificationRegister(), verifyToken);
    }

    private void sendMail(String toEmail, String subject, String content) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(mailProperties.getSender(), mailProperties.getSenderName());
            messageHelper.setTo(toEmail);
            messageHelper.setSubject(subject);
            messageHelper.setText(content, true);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException | UnsupportedEncodingException exception) {
            exception.printStackTrace();
        }
    }

}
