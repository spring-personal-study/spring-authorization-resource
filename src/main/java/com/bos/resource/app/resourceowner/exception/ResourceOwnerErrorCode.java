package com.bos.resource.app.resourceowner.exception;

import com.bos.resource.app.fota.exception.FOTACrudErrorCode;
import com.bos.resource.exception.common.ApiErrorMessage;
import com.bos.resource.exception.common.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum ResourceOwnerErrorCode implements ErrorCode {
    RESOURCE_OWNER_CRUD_FAIL(HttpStatus.BAD_REQUEST, -1000, ApiErrorMessage.RESOURCE_OWNER_CRUD_FAIL),
    RESOURCE_OWNER_NOT_FOUND(HttpStatus.NOT_FOUND, -1001, ApiErrorMessage.RESOURCE_OWNER_NOT_FOUND),
    ;

    private static final Map<String, ResourceOwnerErrorCode> bizCodes =
            Collections.unmodifiableMap(Stream.of(values())
                    .collect(Collectors.toMap(ResourceOwnerErrorCode::getMsg, Function.identity())));

    private final HttpStatus httpStatus;
    private final Integer bizCode;
    private final String msg;

    public Integer findMatchBizCode(final String failMessage) {
        return bizCodes.get(failMessage).getBizCode();
    }
}
