// exception/GeneralException.java (Updated)
package com.example.volunteer_platform.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500
public class GeneralException extends RuntimeException {
    public GeneralException(String message) {
        super(message);
    }

    // Added constructor to accept message and Throwable (for wrapping exceptions)
    public GeneralException(String message, Throwable cause) {
        super(message, cause);
    }
}