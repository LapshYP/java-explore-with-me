package ru.practicum.ewmservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ExceptionController {


    //400
    @ExceptionHandler({BadRequestException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> badRequestException(BadRequestException ex) {
        String erroMessage = ex.getMessage() != null ? ex.getMessage() : "the object has wrong fields";

        return Map.of("BadRequestException ", erroMessage);
    }

    //404+
    @ExceptionHandler({NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> notFoundException(NotFoundException ex) {
        String erroMessage = ex.getMessage() != null ? ex.getMessage() : "the object does not exists";

        return Map.of("NotFoundException ", erroMessage);
    }

    //500
    @ExceptionHandler({MissingRequestHeaderException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleException(MissingRequestHeaderException ex) {
        String erroMessage = ex.getMessage() != null ? ex.getMessage() : "the header don't exists";
        return Map.of("MissingRequestHeaderException ", erroMessage);
    }

    //500
    @ExceptionHandler({MissingPathVariableException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleException(MissingPathVariableException ex) {
        String erroMessage = ex.getMessage() != null ? ex.getMessage() : "the pathVariable don't exists";

        return Map.of("MissingPathVariableException  ", erroMessage);
    }

    //500+
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleException(final UnsupportedStatusException ex) {
        String msg = "{\"error\":\"Unknown state: UNSUPPORTED_STATUS\",\n" +
                "\"message\":\"UNSUPPORTED_STATUS\"}";

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorTextBuilder methodArgumentNotValidException(final MethodArgumentNotValidException ex) {
        log.error("Exception 400. {}", ex.getMessage());

        ErrorTextBuilder errorTextBuilder = new ErrorTextBuilder(ex.getMessage(),
                "Exception handle method argument.", HttpStatus.BAD_REQUEST);
        return errorTextBuilder;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorTextBuilder constraintViolationException(final ConstraintViolationException ex) {
        log.error("Exception 400 . {}", ex.getMessage());

        ErrorTextBuilder errorTextBuilder = new ErrorTextBuilder(ex.getMessage(),
                "Bad validation.", HttpStatus.BAD_REQUEST);
        return errorTextBuilder;
    }


    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorTextBuilder sqlException(final SQLException ex) {
        log.error("Exception 409 . {}", ex.getMessage());
        ErrorTextBuilder errorTextBuilder = new ErrorTextBuilder(ex.getMessage(),
                "Incorrectly made request.",
                HttpStatus.CONFLICT);
        return errorTextBuilder;
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorTextBuilder conflictException(final ConflictException ex) {
        log.error("Ошибка 409 . {}", ex.getMessage());
        ErrorTextBuilder errorTextBuilder = new ErrorTextBuilder(ex.getMessage(),
                "Incorrectly made request.",
                HttpStatus.CONFLICT);
        return errorTextBuilder;
    }


}
