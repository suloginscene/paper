package me.scene.dinner.account.domain.tempaccount.exception;

public class VerificationException extends IllegalArgumentException {

    public VerificationException(String token) {
        super(token);
    }

}