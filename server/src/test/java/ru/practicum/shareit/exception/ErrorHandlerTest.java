package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    public void handleNotFoundException_ShouldReturnNotFound() {
        NotFoundException exception = new NotFoundException("Not found");

        Map<String, String> result = errorHandler.handleNotFoundException(exception);

        assertNotNull(result);
        assertEquals("Not found", result.get("error"));
    }

    @Test
    public void handleValidationException_ShouldReturnBadRequest() {
        ValidationException exception = new ValidationException("Validation failed");

        Map<String, String> result = errorHandler.handleValidationException(exception);

        assertNotNull(result);
        assertEquals("Validation failed", result.get("error"));
    }

    @Test
    public void handleResponseStatusException_ShouldReturnProperResponse() {
        ResponseStatusException exception = new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");

        ResponseEntity<Map<String, String>> result = errorHandler.handleResponseStatusException(exception);

        assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Bad request", result.getBody().get("error"));
    }
}
