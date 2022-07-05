package com.tusofia.diplomna.service.emailSender;

import org.springframework.mail.SimpleMailMessage;

public interface EmailSenderService {

    public void sendEmail(SimpleMailMessage email);
}
