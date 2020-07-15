package ua.training.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class RegException extends Exception {

    public RegException(String message) {
        super(message);
    }
}

