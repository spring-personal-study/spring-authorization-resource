package com.bos.resource.exception.controllerAdvice;

import com.bos.emm.exception.common.BizException;
import com.bos.emm.exception.common.ErrorResponseDTO;
import com.bos.emm.exception.common.GeneralErrorMessage;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.net.MalformedURLException;

@Slf4j
@Order(value = Ordered.HIGHEST_PRECEDENCE + 2)
@RestControllerAdvice
public class MainControllerAdvice {

    /**
     * It is triggered when an error occurs in the code
     * that is not expected and the exception is not handled.
     *
     * @param e Global error handling
     * @return 500
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponseDTO> handleException(Exception e) {
        return GeneralControllerAdvice.handleGeneralException(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }

    @ExceptionHandler(MismatchedInputException.class)
    protected ResponseEntity<ErrorResponseDTO> handleMismatchedInputException(MethodArgumentNotValidException e) {
        return GeneralControllerAdvice.handleGeneralException(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }

    /**
     * It works when the argument value is NULL.
     *
     * @return 500
     */
    @ExceptionHandler(NullPointerException.class)
    protected ResponseEntity<ErrorResponseDTO> handleNPE(NullPointerException e) {
        return GeneralControllerAdvice.handleGeneralException(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }

    /**
     * Occurs when all parameters in @Valid are empty.
     *
     * @return 400
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> catchIllegalArgumentException(HttpMessageNotReadableException e) {
        // direct message custom
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(GeneralErrorMessage.ALL_PARAMETER_IS_NULL)
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .build();
        log.error(e.getMessage());
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles requests with invalid arguments.
     * <p>
     * exp: StrangeProtocol://www.homepage.com/...
     * exp: http://www.homepage.com?data={HackingAttack}
     * <p>
     * IllegalArgumentException and MalformedURLException are a bunch.
     *
     * @return 400
     */
    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ErrorResponseDTO> handleIllgegalURLException(IllegalArgumentException iae) {
        return GeneralControllerAdvice.handleGeneralException(HttpStatus.BAD_REQUEST, iae);
    }

    @ExceptionHandler(MalformedURLException.class)
    protected ResponseEntity<ErrorResponseDTO> handleMalformedURLException(MalformedURLException mue) {
        return GeneralControllerAdvice.handleGeneralException(HttpStatus.BAD_REQUEST, mue);
    }

    /**
     * When a 404 error occurs, it redirects to the default designated error page.
     *
     * @return 404
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    protected ResponseEntity<ErrorResponseDTO> handle404(NoHandlerFoundException nhfe) {
        log.error("A user requested a resource with a non-existent url path.");
        return GeneralControllerAdvice.handleGeneralException(HttpStatus.NOT_FOUND, nhfe);
    }

    /**
     * It works when a request comes with an http mapping method
     * that is not supported by this server.
     * (Example: Behavior when a client requests a specific method
     * that supports only get in the post method)
     *
     * @param e HttpRequestMethodNotSupportedException
     * @return 405
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponseDTO> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return GeneralControllerAdvice.handleGeneralException(HttpStatus.METHOD_NOT_ALLOWED, e);
    }

    /**
     * Protocol abnormal error handling
     * Occurs when an invalid protocol connection is attempted or a protocol connection failure occurs.
     *
     * @param e BadGateway 502
     * @return 502
     */
    @ExceptionHandler(HttpServerErrorException.BadGateway.class)
    protected ResponseEntity<ErrorResponseDTO> handleBadGateway(HttpServerErrorException.BadGateway e) {
        return GeneralControllerAdvice.handleGeneralException(HttpStatus.BAD_GATEWAY, e);
    }

    /**
     * It operates when the server cannot process the request
     * because there are too many server accessors
     * or the server fails to process the request normally.
     *
     * @param e Service Unavailable 503
     * @return 503
     */
    @ExceptionHandler(HttpServerErrorException.ServiceUnavailable.class)
    protected ResponseEntity<ErrorResponseDTO> handleServiceUnavailable(HttpServerErrorException.ServiceUnavailable e) {
        return GeneralControllerAdvice.handleGeneralException(HttpStatus.SERVICE_UNAVAILABLE, e);
    }

    /**
     * 400
     *
     * @param e throw e from validation failed
     * @return Return 400 Bad Request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponseDTO> handleMethodArgNotValidException(MethodArgumentNotValidException e) {
        return GeneralControllerAdvice.handleGeneralException(HttpStatus.BAD_REQUEST, e);
    }

    /**
     * () -> throw new BizException(e)
     * If written in the above way,
     * this method will handle the error for this exception handling.
     *
     * @param e throw e from new BizException("e")
     * @return Return the information of e passed to the constructor in new BizException(e)
     */
    @ExceptionHandler(BizException.class)
    public ResponseEntity<ErrorResponseDTO> catchBizException(BizException e) {
        return GeneralControllerAdvice.handleBizException(e.getHttpStatus(), e);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponseDTO> catchFileSizeException(MaxUploadSizeExceededException e){
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
            .message(GeneralErrorMessage.MAXIMUM_FILE_SIZE)
            .internalCode(-8001)
            .errorCode(HttpStatus.BAD_REQUEST.value())
            .build();
        log.error(e.getMessage());
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }


}
