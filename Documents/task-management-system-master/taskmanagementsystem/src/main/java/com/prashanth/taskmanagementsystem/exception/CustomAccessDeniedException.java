package com.prashanth.taskmanagementsystem.exception;

import org.springframework.security.access.AccessDeniedException;

public class CustomAccessDeniedException extends AccessDeniedException {
    public CustomAccessDeniedException(String message) {
        super(message);
    }
}
