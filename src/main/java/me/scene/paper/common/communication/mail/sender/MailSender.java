package me.scene.paper.common.communication.mail.sender;

import me.scene.paper.common.communication.mail.message.MailMessage;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;


@Component
public abstract class MailSender {

    protected abstract void doSend(String subject, String to, String message) throws MessagingException;

    public void send(MailMessage mailMessage) {
        String subject = mailMessage.getSubject();
        String to = mailMessage.getTo();
        String message = mailMessage.getMessage();

        try {
            doSend(subject, to, message);
        } catch (MessagingException e) {
            throw new MailException(e.getMessage());
        }
    }

}
