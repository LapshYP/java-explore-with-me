//package ru.practicum.ewmservice.exception;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//import javax.validation.ConstraintViolationException;
//import java.sql.SQLException;
//
//@Slf4j
//@RestControllerAdvice
//public class ErrorHandler {
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ApiError handleMethodArgumentNotValidException(final MethodArgumentNotValidException ex) {
//        log.error("Ошибка 400 (MethodArgumentNotValidException). {}", ex.getMessage());
//
//        ApiError apiError = new ApiError(ex.getMessage(),
//                "Ошибка во входных данных примитивов.", HttpStatus.BAD_REQUEST);
//        return apiError;
//    }
//
//    @ExceptionHandler(ConstraintViolationException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ApiError handleConstraintViolationException(final ConstraintViolationException ex) {
//        log.error("Ошибка 400 (ConstraintViolationException). {}", ex.getMessage());
//
//        ApiError apiError = new ApiError(ex.getMessage(),
//                "Ошибка во входных данных сложных объектов.", HttpStatus.BAD_REQUEST);
//        return apiError;
//    }
//
//
//    @ExceptionHandler(SQLException.class)
//    @ResponseStatus(HttpStatus.CONFLICT)
//    public ApiError handleSQLException(final SQLException ex) {
//        log.error("Ошибка 409 (SQLException). {}", ex.getMessage());
//        ApiError apiError = new ApiError(ex.getMessage(),
//                "Запрашиваемая операция не может быть выполнена.",
//                HttpStatus.CONFLICT);
//        return apiError;
//    }
//}
