package kpi.zaranik.kexitdrive.core.controller;

import java.time.LocalDateTime;
import kpi.zaranik.kexitdrive.core.exception.AccessToResourceDeniedException;
import kpi.zaranik.kexitdrive.core.exception.NoSuitableConverterServiceFoundException;
import kpi.zaranik.kexitdrive.core.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AccessToResourceDeniedException.class)
    public ProblemDetail handleAccessToResourceDeniedException(AccessToResourceDeniedException exception) {
        log.warn(exception.getMessage(), exception);
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.getMessage());
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        return problemDetail;
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ProblemDetail handleUnsupportedOperationException(UnsupportedOperationException exception) {
        log.warn(exception.getMessage(), exception);
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        return problemDetail;
    }

    @ExceptionHandler(NoSuitableConverterServiceFoundException.class)
    public ProblemDetail handleNoSuitableConverterServiceFoundException(NoSuitableConverterServiceFoundException exception) {
        log.warn(exception.getMessage(), exception);
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        return problemDetail;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFoundException(ResourceNotFoundException exception) {
        log.warn(exception.getMessage(), exception);
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        return problemDetail;
    }

}
