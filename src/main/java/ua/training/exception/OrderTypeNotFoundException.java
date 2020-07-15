package ua.training.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class OrderTypeNotFoundException extends Exception {

    public OrderTypeNotFoundException(String message) {
        super(message);
    }
}
