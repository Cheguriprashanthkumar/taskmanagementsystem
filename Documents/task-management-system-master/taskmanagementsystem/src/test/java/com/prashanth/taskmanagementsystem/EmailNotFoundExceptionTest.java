package com.prashanth.taskmanagementsystem;

import com.prashanth.taskmanagementsystem.exception.EmailNotFoundException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EmailNotFoundExceptionTest {

    @Test
    void shouldReturnCorrectMessage() {
        String message = "Email not found";
        EmailNotFoundException exception = new EmailNotFoundException(message);

        assertThat(exception.getMessage()).isEqualTo(message);
    }
}
