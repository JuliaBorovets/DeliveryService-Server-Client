package ua.training.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BankCardException extends Exception {

    public BankCardException(String message) {
        super(message);
    }
}
