package me.scene.dinner.mail.infra.sender;

import lombok.extern.slf4j.Slf4j;
import me.scene.dinner.mail.application.sender.MailSender;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile({"local", "test"})
@Slf4j
@Component
public class ConsoleMailSender extends MailSender {

    private static final StringBuilder sb = new StringBuilder();

    @Override
    protected void doSend(String subject, String to, String message) {
        sb.setLength(0);
        sb.append("\n")
                .append("\t").append("subject: ").append(subject).append("\n")
                .append("\t").append("to     : ").append(to).append("\n")
                .append("\t").append("text   : ").append(message).append("\n").append("\n");
        log.info(sb.toString());
    }

}