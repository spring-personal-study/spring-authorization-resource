package com.bos.resource.exception.controllerAdvice;

import com.bos.resource.exception.common.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Objects;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public class GeneralControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(GeneralControllerAdvice.class);

    /**
     * for Global exception handling only.
     *
     * @return ResponseEntity<ErrorResponseDTO>
     */
    public static ResponseEntity<ErrorResponseDTO> handleUnknownInternalException(Exception... e) {
        HttpStatus httpStatus = INTERNAL_SERVER_ERROR;
        ErrorResponseDTO response = ErrorResponseDTO.builder()
                .errorCode(httpStatus.value())
                .message("Internal Server Error")
                .build();

        log.error("handleUnknownInternalException()");
        log.error(Arrays.stream(e)
                .filter(Objects::nonNull).findFirst()
                .map(Exception::getMessage)
                .orElseGet(httpStatus::getReasonPhrase));

        return new ResponseEntity<>(response, getHttpHeader(), httpStatus);
    }


    /**
     * Creates a standardized error response message format.
     *
     * @param httpStatus an http error occurred
     * @param e          List of exceptions that validate @Valid or @Validated
     * @return ResponseEntity<ErrorResponseDTO>
     */
    public static ResponseEntity<ErrorResponseDTO> handleGeneralException(HttpStatus httpStatus, Exception... e) {
        ErrorResponseDTO response = ErrorResponseDTO.builder()
                .errorCode(httpStatus.value())
                .message(Arrays.stream(e)
                        .filter(Objects::nonNull).findFirst()
                        .map(Exception::getMessage)
                        .orElseGet(httpStatus::getReasonPhrase))
                .build();

        log.error(response.getMsg());

        return new ResponseEntity<>(response, getHttpHeader(), httpStatus);
    }

    /**
     * Creates a standardized error response message format.
     *
     * @param httpStatus an http error occurred
     * @param e          List of exceptions that validate @Valid or @Validated
     * @return ResponseEntity<ErrorResponseDTO>
     */
    public static ResponseEntity<ErrorResponseDTO> handleBizException(HttpStatus httpStatus, BizException e) {
        ErrorResponseDTO response = ErrorResponseDTO.builder()
                .errorCode(httpStatus.value())
                .message(e.getLocalizedMessage())
                .internalCode(e.getBizCode())
                .build();

        log.error(response.getMsg());

        return new ResponseEntity<>(response, getHttpHeader(), httpStatus);
    }

    /**
     * 정Creates a standardized error contains paramaters response message format.
     * This method is used for exceptions that get BindingResult information through @Valid validation.
     *
     * @param httpStatus an http error occurred
     * @param e          List of exceptions that validate @Valid or @Validated
     * @return ResponseEntity<ErrorResponseDTO>
     */
    public static ResponseEntity<ErrorResponseDTO> handleValidParameterException(HttpStatus httpStatus, ErrorCode errorCode, InvalidParameterException... e) {
        ErrorResponseDTO response = ErrorResponseDTO.builder()
                .errorCode(httpStatus.value())
                .message(Arrays.stream(e)
                        .filter(Objects::nonNull).findFirst()
                        .map(Exception::getMessage)
                        .orElse(httpStatus.toString()))
                .errors(Arrays.stream(e)
                        .filter(Objects::nonNull)
                        .findFirst()
                        .orElseThrow(() -> new BizException(GeneralParameterErrorCode.INVALID_PARAMETER))
                        .getErrors(), errorCode)
                .internalCode(errorCode.getBizCode())
                .build();

        log.error(response.getMsg());

        return new ResponseEntity<>(response, getHttpHeader(), httpStatus);
    }

    /**
     * Setting the error information header to be forwarded to the client.
     *
     * @return error response header
     */
    private static HttpHeaders getHttpHeader() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        return responseHeaders;
    }

}