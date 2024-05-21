package com.otg.tech.notification.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.otg.tech.commons.ApiResponse;
import com.otg.tech.commons.ErrorResponse;
import com.otg.tech.exception.ApplicationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.otg.tech.notification.domain.enums.ErrorExceptionCodes.NOTIFY0400;
import static com.otg.tech.notification.domain.enums.ErrorExceptionCodes.NOTIFY0500;

@RestControllerAdvice
@SuppressWarnings("unused")
public class NotificationExceptionHandler {

    private static ErrorResponse getErrorResponse(InvalidFormatException ifx) {
        var errorDetails = String.format("Invalid enum value: '%s' for the field: '%s'. "
                        + "The value must be one of: %s.",
                ifx.getValue(), ifx.getPath().get(ifx.getPath().size() - 1).getFieldName(),
                Arrays.toString(ifx.getTargetType().getEnumConstants()));

        return new ErrorResponse(NOTIFY0400.getHttpStatus(), NOTIFY0400.getCode(),
                NOTIFY0400.getMessage(), Collections.singletonList(errorDetails));
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    @ResponseStatus(value = HttpStatus.OK)
    ApiResponse<Void> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }
        return ApiResponse.error(new ErrorResponse(NOTIFY0400.getHttpStatus(), NOTIFY0400.getCode(),
                NOTIFY0400.getMessage(), errors));
    }

    @ExceptionHandler(value = {ResponseStatusException.class})
    @ResponseStatus(value = HttpStatus.OK)
    ApiResponse<Void> handleResponseStatusException(ResponseStatusException ex) {
        return ApiResponse.error(new ErrorResponse(ex.getStatusCode().value(), ex.getReason()));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiResponse<ErrorResponse> handleException(Exception e) {
        if (e.getCause() instanceof InvalidFormatException ifx
                && (ifx.getTargetType() != null && ifx.getTargetType().isEnum())) {
            ErrorResponse response = getErrorResponse(ifx);

            return ApiResponse.error(response);
        }
        ErrorResponse response = new ErrorResponse(NOTIFY0500.getHttpStatus(), NOTIFY0500.getCode(),
                NOTIFY0500.getMessage());
        return ApiResponse.error(response);
    }

    @ExceptionHandler(ApplicationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiResponse<ErrorResponse> handleApplicationException(ApplicationException e) {
        ErrorResponse response = new ErrorResponse(e.getCode(), e.getErrorCode(), e.getMessage());
        return ApiResponse.error(response);
    }
}
