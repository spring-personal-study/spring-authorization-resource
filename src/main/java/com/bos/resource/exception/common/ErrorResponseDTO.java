package com.bos.resource.exception.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

@Getter
public class ErrorResponseDTO {

    private final String msg;
    private final Integer httpCode;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Integer internalCode;
    /**
     * If you set a value for customFieldErrors in the same way as errors(BindingResult result),
     * the json key value called errors is included in the error handling response message and delivered.
     * If not set, the errors json key value is not passed to the client.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final List<CustomFieldError> detailErrors;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime timestamp;

    private ErrorResponseDTO(ErrorResponseDTOBuilder builder) {
        if (builder.displayNow) this.timestamp = LocalDateTime.now();
        this.msg = builder.message;
        this.internalCode = builder.internalCode;
        this.httpCode = builder.errorCode;
        this.detailErrors = builder.customFieldErrors;
    }

    public static ErrorResponseDTOBuilder builder() {
        return new ErrorResponseDTOBuilder();
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE, false, false, true, null);
    }

    @NoArgsConstructor
    public static class ErrorResponseDTOBuilder {
        private boolean displayNow = true;
        private String message;
        private Integer errorCode;
        private Integer internalCode;
        private List<CustomFieldError> customFieldErrors;

        public ErrorResponseDTOBuilder errorCode(Integer code) {
            this.errorCode = code;
            return this;
        }

        public ErrorResponseDTOBuilder message(String message) {
            this.message = message;
            return this;
        }

        public ErrorResponseDTOBuilder internalCode(Integer internalCode) {
            this.internalCode = internalCode;
            return this;
        }

        /**
         * The errors' method should only be used when using (@Valid or @Validated) and BindingResult .
         * If errorCode is set, the previously agreed error code can be delivered to the client together.
         * (In case of using overloading method redundantly) It is applied as the last called setting information.
         *
         * @param errors from BindingResult.getFieldErrors()
         * @return builder method chained ErrorResponseDTO
         */
        public ErrorResponseDTOBuilder errors(Errors errors, ErrorCode errorCode) {
            setCustomFieldErrors(errors.getFieldErrors(), errorCode);
            return this;
        }

        /**
         * fieldErrors and Enumerate errorCode passed through the BindingResult.getFieldErrors() method
         */
        private void setCustomFieldErrors(List<FieldError> fieldErrors, ErrorCode errorCode) {

            customFieldErrors = new ArrayList<>();

            for (FieldError fieldError : fieldErrors) {
                System.out.println(Arrays.toString(fieldError.getCodes()));
                customFieldErrors.add(new CustomFieldError(
                        Arrays.stream(requireNonNull(fieldError.getCodes())[1].split("\\.")).skip(1).collect(joining(".")),
                        fieldError.getRejectedValue(),
                        fieldError.getDefaultMessage(),
                        errorCode.findMatchBizCode(fieldError.getDefaultMessage()))
                );
            }
        }

        /**
         * Disable the timestamp output.
         *
         * @return timestamp false
         */
        public ErrorResponseDTOBuilder offDisplayTimeStamp() {
            this.displayNow = false;
            return this;
        }

        public ErrorResponseDTO build() {
            return new ErrorResponseDTO(this);
        }
    }

    /**
     * A class containing fields that did not pass parameter validation by @Valid or @Validated
     */
    public record CustomFieldError(String rejectedParameter, Object rejectedValue, String reason,
                                   Integer internalErrorCode) {
    }
}