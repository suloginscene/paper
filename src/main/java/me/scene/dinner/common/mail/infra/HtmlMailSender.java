package me.scene.dinner.common.mail.infra;

import lombok.RequiredArgsConstructor;
import me.scene.dinner.common.mail.service.sender.MailSender;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;


@Component
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class HtmlMailSender extends MailSender {

    private final JavaMailSender sender;


    @Override
    protected void doSend(String subject, String to, String message) throws MessagingException {
        MimeMessage mimeMessage = sender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, StandardCharsets.UTF_8.name());
        helper.setSubject(subject);
        helper.setTo(to);
        helper.setText(message, true);

        sender.send(mimeMessage);
    }

}
