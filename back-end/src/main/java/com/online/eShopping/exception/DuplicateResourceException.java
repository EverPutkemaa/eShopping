package com.online.eShopping.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Erind, mida visatakse kui ressurss juba eksisteerib (nt. sama meiliaadressiga kasutaja).
 */

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateResourceException extends RuntimeException{

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
