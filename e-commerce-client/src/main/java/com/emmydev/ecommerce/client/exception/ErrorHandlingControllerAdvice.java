package com.emmydev.ecommerce.client.exception;

import com.emmydev.ecommerce.client.dto.ValidationErrorResponseDto;
import com.emmydev.ecommerce.client.dto.ViolationDto;
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
    public ValidationErrorResponseDto onConstrainValidationException(ConstraintViolationException ex){
        ValidationErrorResponseDto errorResponse = new ValidationErrorResponseDto();

        for(ConstraintViolation violation: ex.getConstraintViolations()){
            errorResponse.getViolationDtos().add(new ViolationDto(violation.getPropertyPath().toString(), violation.getMessage()));
        }

        return errorResponse;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    public ValidationErrorResponseDto onMethodArgumentNotValidException(MethodArgumentNotValidException ex){

        ValidationErrorResponseDto errorResponse = new ValidationErrorResponseDto();

        for(FieldError fieldError: ex.getBindingResult().getFieldErrors()){
            errorResponse.getViolationDtos().add(new ViolationDto(fieldError.getField(), fieldError.getDefaultMessage()));
        }

        if(ex.hasGlobalErrors()){
            errorResponse.getViolationDtos().add(new ViolationDto(ex.getGlobalError().getObjectName(), ex.getGlobalError().getDefaultMessage()));
        }

        log.info(errorResponse.toString());
        return errorResponse;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationErrorResponseDto onNoSuchObjectException(HttpMessageNotReadableException ex){

        ValidationErrorResponseDto errorResponse = new ValidationErrorResponseDto();
        errorResponse.getViolationDtos().add(new ViolationDto(ex.getClass().getPackage().getClass().getName(), "Missing required fields"));

        log.info(errorResponse.toString());
        return errorResponse;
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationErrorResponseDto onUserAlreadyExistsException(UserAlreadyExistsException ex){

        ValidationErrorResponseDto errorResponse = new ValidationErrorResponseDto();
        errorResponse.getViolationDtos().add(new ViolationDto("Email", ex.getMessage()));

        log.info(errorResponse.toString());
        return errorResponse;
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationErrorResponseDto onUserNotFoundException(UserNotFoundException ex){
        ValidationErrorResponseDto errorResponse = new ValidationErrorResponseDto();
        errorResponse.getViolationDtos().add(new ViolationDto("Email", ex.getMessage()));

        log.error(errorResponse.toString());
        return errorResponse;
    }

    @ExceptionHandler(TokenNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationErrorResponseDto onTokenNotFoundException(TokenNotFoundException ex){
        ValidationErrorResponseDto errorResponse = new ValidationErrorResponseDto();
        errorResponse.getViolationDtos().add(new ViolationDto("VerificationToken", ex.getMessage()));

        log.info(errorResponse.toString());
        return errorResponse;
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ValidationErrorResponseDto onRuntimeException(RuntimeException ex){
        ValidationErrorResponseDto errorResponse = new ValidationErrorResponseDto();
        errorResponse.getViolationDtos().add(new ViolationDto("Runtime error", ex.getMessage()));
        log.error(ex.getMessage());
        log.error(ex.toString());
        return errorResponse;
    }
}
