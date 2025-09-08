package com.usbpd5.parser.exception;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    private ErrorResponse buildErrorResponse(String message, HttpStatus status, HttpServletRequest request) {
        return new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFileNotFound(FileNotFoundException ex, HttpServletRequest request) {
        return new ResponseEntity<>(
                buildErrorResponse("File not found: " + ex.getMessage(), HttpStatus.NOT_FOUND, request),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIOException(IOException ex, HttpServletRequest request) {
        return new ResponseEntity<>(
                buildErrorResponse("I/O error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ErrorResponse> handleMissingPart(MissingServletRequestPartException ex, HttpServletRequest request) {
        return new ResponseEntity<>(
                buildErrorResponse("Missing required part: " + ex.getRequestPartName(), HttpStatus.BAD_REQUEST, request),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxSize(MaxUploadSizeExceededException ex, HttpServletRequest request) {
        return new ResponseEntity<>(
                buildErrorResponse("Uploaded file is too large!", HttpStatus.PAYLOAD_TOO_LARGE, request),
                HttpStatus.PAYLOAD_TOO_LARGE
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        return new ResponseEntity<>(
                buildErrorResponse("Unexpected error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
