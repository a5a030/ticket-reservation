package com.byunsum.ticket_reservation.global.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse>  handleCustomException(CustomException e) {
        ErrorCode code = e.getErrorCode();

        return new ResponseEntity<>(
                new ErrorResponse(code.name(), code.getMessage()), HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse>  handleAccessDenied(AccessDeniedException e) {
        return new ResponseEntity<>(
                new ErrorResponse(ErrorCode.FORBIDDEN.name(), ErrorCode.FORBIDDEN.getMessage()), HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse>  handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors()
                .stream().findFirst().map(err -> err.getField() + ": " + err.getDefaultMessage())
                .orElse("잘못된 요청입니다.");

        return new ResponseEntity<>(
                new ErrorResponse(ErrorCode.INVALID_INPUT_VALUE.name(), message), HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse>  handleException(Exception e) {
        e.printStackTrace();

        return new ResponseEntity<>(
                new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR.name(), ErrorCode.INTERNAL_SERVER_ERROR.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
