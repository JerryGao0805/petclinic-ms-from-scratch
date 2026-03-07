package com.yanggao.petclinic.visits.web.error;

import java.net.URI;
import java.util.List;

import com.yanggao.petclinic.visits.model.Visit;
import com.yanggao.petclinic.visits.service.VisitNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ApiExceptionHandler {

    private record FieldViolation(String field, String message) {}

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Validation failed");
        problem.setType(URI.create("urn:problem-type:validation-error"));
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setDetail("Request body validation failed.");

        List<FieldViolation> violations = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(ApiExceptionHandler::toViolation)
                .toList();

        problem.setProperty("errors", violations);
        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Malformed request body");
        problem.setType(URI.create("urn:problem-type:malformed-json"));
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setDetail("Request body is malformed or contains invalid field types.");
        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler(VisitNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleVisitNotFound(
             VisitNotFoundException ex,
            HttpServletRequest request
    ) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("Visit not found");
        problem.setType(URI.create("urn:problem-type:not-found"));
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setDetail(ex.getMessage());
        problem.setProperty("id", ex.getId());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ProblemDetail> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpServletRequest request
    ) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Missing request parameter");
        problem.setType(URI.create("urn:problem-type:missing-parameter"));
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setDetail("Required request parameter '%s' is missing.".formatted(ex.getParameterName()));
        problem.setProperty("parameter", ex.getParameterName());
        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request
    ) {
        String expectedType = (ex.getRequiredType() != null) ? ex.getRequiredType().getSimpleName() : "unknown";

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Invalid parameter");
        problem.setType(URI.create("urn:problem-type:invalid-parameter"));
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setDetail("Parameter '%s' must be a valid %s.".formatted(ex.getName(), expectedType));

        problem.setProperty("parameter", ex.getName());
        problem.setProperty("value", ex.getValue());
        problem.setProperty("expectedType", expectedType);

        return ResponseEntity.badRequest().body(problem);
    }


    private static FieldViolation toViolation(FieldError error) {
        String message = (error.getDefaultMessage() != null) ? error.getDefaultMessage() : "invalid";
        return new FieldViolation(error.getField(), message);
    }
}

