package com.byunsum.ticket_reservation.global.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse>  handleCustomException(CustomException e) {
        ErrorCode code = e.getErrorCode();

        return new ResponseEntity<>(
                new ErrorResponse(code, e.getMessage()), code.getStatus()
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse>  handleAccessDenied(AccessDeniedException e) {
        ErrorCode code = ErrorCode.FORBIDDEN;

        return new ResponseEntity<>(
                new ErrorResponse(code, code.getMessage()), code.getStatus()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse>  handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors()
                .stream().findFirst().map(err -> err.getField() + ": " + err.getDefaultMessage())
                .orElse(ErrorCode.INVALID_INPUT_VALUE.getMessage());

        ErrorCode code = ErrorCode.INVALID_INPUT_VALUE;

        return new ResponseEntity<>(
                new ErrorResponse(code, message), code.getStatus()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse>  handleException(Exception e) {
        logger.error("Unhandled exception", e);

        ErrorCode code = ErrorCode.INTERNAL_SERVER_ERROR;

        return new ResponseEntity<>(
                new ErrorResponse(code), code.getStatus()
        );
    }
}
