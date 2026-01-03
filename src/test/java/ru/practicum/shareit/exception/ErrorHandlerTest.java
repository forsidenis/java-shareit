package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;

public class ErrorHandlerTest {

    private ErrorHandler errorHandler = new ErrorHandler();

    @Test
    public void handleNotFoundException_returnsNotFoundStatus() {
        RuntimeException exception = new RuntimeException("not found");

        ResponseEntity<ErrorHandler.ErrorResponse> response =
                errorHandler.handleNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("not found", response.getBody().getError());
    }

    @Test
    public void handleValidationException_returnsBadRequestStatus() {
        IllegalArgumentException exception = new IllegalArgumentException("Invalid input");

        ResponseEntity<ErrorHandler.ErrorResponse> response =
                errorHandler.handleValidationException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid input", response.getBody().getError());
    }

    @Test
    public void handleForbiddenException_returnsForbiddenStatus() {
        SecurityException exception = new SecurityException("Access denied");

        ResponseEntity<ErrorHandler.ErrorResponse> response =
                errorHandler.handleForbiddenException(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Access denied", response.getBody().getError());
    }

    @Test
    public void handleInternalServerError_returnsInternalServerErrorStatus() {
        Exception exception = new Exception("Test exception");

        ResponseEntity<ErrorHandler.ErrorResponse> response =
                errorHandler.handleInternalServerError(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal server error", response.getBody().getError());
    }
}
