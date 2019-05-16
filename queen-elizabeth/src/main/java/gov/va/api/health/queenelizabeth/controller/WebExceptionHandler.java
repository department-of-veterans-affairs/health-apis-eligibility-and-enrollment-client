package gov.va.api.health.queenelizabeth.controller;

import gov.va.api.health.queenelizabeth.ee.Eligibilities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Exceptions that escape the rest controllers will be processed by this handler. It will convert
 * exception into different HTTP status codes and produce an error response payload.
 */
@RestControllerAdvice
@RequestMapping(produces = {"application/json", "application/xml"})
@Slf4j
public class WebExceptionHandler {
  @ExceptionHandler({
    Eligibilities.MissingIcnValue.class,
    javax.validation.ConstraintViolationException.class
  })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleBadRequest(Exception e) {
    return responseFor(e);
  }

  @ExceptionHandler({Exception.class})
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponse handleSnafu(Exception e) {
    return responseFor(e);
  }

  private ErrorResponse responseFor(Exception e) {
    ErrorResponse response = ErrorResponse.of(e);
    log.error("{}: {}", response.type(), response.message(), e);
    return response;
  }
}
