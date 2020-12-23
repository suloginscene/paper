package me.scene.dinner.common.mail.service.sender;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(exclude = "message")
public abstract class MailMessage {

    private final String subject;
    private final String to;
    private final String message;

}