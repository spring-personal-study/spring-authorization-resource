package com.bos.resource.exception.common;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BizException extends RuntimeException {
    private final String message;
    private final Integer bizCode;
    private final HttpStatus httpStatus;

    public BizException(ErrorCode code) {
        this.message = code.getMsg();
        this.bizCode = code.getBizCode();
        this.httpStatus = code.getHttpStatus();
    }

    public BizException(CustomMsg customMsg) {
        this.message = customMsg.failMsg();
        this.bizCode = customMsg.bizCode();
        this.httpStatus = customMsg.httpStatus();
    }

    @Builder
    public record CustomMsg(String failMsg, Integer bizCode, HttpStatus httpStatus) { }
}
