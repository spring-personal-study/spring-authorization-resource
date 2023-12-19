package com.bos.resource.app.fota.exception;


import com.bos.resource.exception.common.ErrorResponseDTO;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.bos.resource.exception.controllerAdvice.GeneralControllerAdvice.handleValidParameterException;

@Order(value = Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class FOTAControllerAdvice {

    /**
     * Operates when validation fails through @Valid or @Validated annotations.
     * If you set the errors' argument value
     * when handling exception responses for controller methods that use @Valid or @Validated annotations,
     * more detailed error messages can be delivered to the client.
     *
     * @param e InvalidParameterException
     * @return 400 (Bad Request: invalid parameter error)
     * @see ErrorResponseDTO
     */
    @ExceptionHandler(InvalidFOTAParameterException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidPostParameterException(InvalidFOTAParameterException e) {
        return handleValidParameterException(e.getHttpStatus(), e.getErrorCode(), e);
    }
}