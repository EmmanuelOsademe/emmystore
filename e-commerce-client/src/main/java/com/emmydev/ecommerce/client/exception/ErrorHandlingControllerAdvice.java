package com.emmydev.ecommerce.client.exception;

import com.emmydev.ecommerce.client.model.ValidationErrorResponse;
import com.emmydev.ecommerce.client.model.Violation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

@Slf4j
@ControllerAdvice
public class ErrorHandlingControllerAdvice  {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationErrorResponse onConstrainValidationException(ConstraintViolationException ex){
        ValidationErrorResponse errorResponse = new ValidationErrorResponse();

        for(ConstraintViolation violation: ex.getConstraintViolations()){
            errorResponse.getViolations().add(new Violation(violation.getPropertyPath().toString(), violation.getMessage()));
        }

        return errorResponse;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    public ValidationErrorResponse onMethodArgumentNotValidException(MethodArgumentNotValidException ex){

        ValidationErrorResponse errorResponse = new ValidationErrorResponse();

        for(FieldError fieldError: ex.getBindingResult().getFieldErrors()){
            errorResponse.getViolations().add(new Violation(fieldError.getField(), fieldError.getDefaultMessage()));
        }

        if(ex.hasGlobalErrors()){
            errorResponse.getViolations().add(new Violation(ex.getGlobalError().getObjectName(), ex.getGlobalError().getDefaultMessage()));
        }

        log.info(errorResponse.toString());
        return errorResponse;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationErrorResponse onNoSuchObjectException(HttpMessageNotReadableException ex){

        ValidationErrorResponse errorResponse = new ValidationErrorResponse();
        errorResponse.getViolations().add(new Violation(ex.getClass().getPackage().getClass().getName(), "Missing required fields"));

        log.info(errorResponse.toString());
        return errorResponse;
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationErrorResponse onUserAlreadyExistsException(UserAlreadyExistsException ex){

        ValidationErrorResponse errorResponse = new ValidationErrorResponse();
        errorResponse.getViolations().add(new Violation("Email", ex.getMessage()));

        log.info(errorResponse.toString());
        return errorResponse;
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationErrorResponse onUserNotFoundException(UserNotFoundException ex){
        ValidationErrorResponse errorResponse = new ValidationErrorResponse();
        errorResponse.getViolations().add(new Violation("Email", ex.getMessage()));

        log.error(errorResponse.toString());
        return errorResponse;
    }

    @ExceptionHandler(TokenNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationErrorResponse onTokenNotFoundException(TokenNotFoundException ex){
        ValidationErrorResponse errorResponse = new ValidationErrorResponse();
        errorResponse.getViolations().add(new Violation("VerificationToken", ex.getMessage()));

        log.info(errorResponse.toString());
        return errorResponse;
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ValidationErrorResponse onRuntimeException(RuntimeException ex){
        ValidationErrorResponse errorResponse = new ValidationErrorResponse();
        errorResponse.getViolations().add(new Violation("Runtime error", ex.getMessage()));
        log.error(ex.toString());
        return errorResponse;
    }
}
