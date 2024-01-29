package com.bos.resource.app.fota.exception;

import com.bos.resource.exception.common.ApiErrorMessage;
import com.bos.resource.exception.common.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
@RequiredArgsConstructor
public enum FOTACrudErrorCode implements ErrorCode {

    FOTA_CRUD_FAIL(BAD_REQUEST, -1401, ApiErrorMessage.FOTA_CRUD_FAIL),
    FIRMWARE_NOT_FOUND(NOT_FOUND, -1402, ApiErrorMessage.FIRMWARE_NOT_FOUND),
    PACKAGE_NOT_FOUND(NOT_FOUND, -1403, ApiErrorMessage.PACKAGE_NOT_FOUND),
    SUPPORT_MODEL_NOT_FOUND(NOT_FOUND, -1404, ApiErrorMessage.SUPPORT_MODEL_NOT_FOUND),
    INCREMENTAL_FIRMWARE_NOT_FOUND(NOT_FOUND, -1405, ApiErrorMessage.INCREMENTAL_FIRMWARE_NOT_FOUND),
    CAMPAIGN_NOT_FOUND(NOT_FOUND, -1406, ApiErrorMessage.CAMPAIGN_NOT_FOUND),
    ATTEMPTED_CANCEL_CAMPAIGN_WITH_NOT_VALID_USER(BAD_REQUEST, -1407, ApiErrorMessage.ATTEMPTED_CANCEL_CAMPAIGN_WITH_NOT_VALID_USER),
    INSERT_JSON_DATA_FAIL(BAD_REQUEST, -1408, ApiErrorMessage.INSERT_JSON_DATA_FAIL),
    NOTIFICATION_TYPE_IS_NULL(BAD_REQUEST, -1409, ApiErrorMessage.NOTIFICATION_TYPE_IS_NULL),
    NOTIFICATION_TYPE_IS_EMPTY(BAD_REQUEST, -1410, ApiErrorMessage.NOTIFICATION_TYPE_IS_EMPTY),
    FOTA_READY_IS_NULL(BAD_REQUEST, -1411, ApiErrorMessage.FOTA_READY_IS_NULL),
    FOTA_READY_IS_EMPTY(BAD_REQUEST, -1412, ApiErrorMessage.FOTA_READY_IS_EMPTY),
    DETAIL_LEVEL_IS_NULL(BAD_REQUEST, -1413, ApiErrorMessage.DETAIL_LEVEL_IS_NULL),
    DETAIL_LEVEL_IS_EMPTY(BAD_REQUEST, -1414, ApiErrorMessage.DETAIL_LEVEL_IS_EMPTY),
    DEPLOYMENT_ID_IS_NULL(BAD_REQUEST, -1415, ApiErrorMessage.DEPLOYMENT_ID_IS_NULL),
    DEPLOYMENT_ID_IS_EMPTY(BAD_REQUEST, -1416, ApiErrorMessage.DEPLOYMENT_ID_IS_EMPTY),
    APPEND_STATUS_IS_NULL(BAD_REQUEST, -1417, ApiErrorMessage.APPEND_STATUS_IS_NULL),
    UPDATE_TYPE_IS_NULL(BAD_REQUEST, -1418, ApiErrorMessage.UPDATE_TYPE_IS_NULL),
    UPDATE_TYPE_IS_EMPTY(BAD_REQUEST, -1419, ApiErrorMessage.UPDATE_TYPE_IS_EMPTY),
    ARTIFACT_NAME_IS_NULL(BAD_REQUEST, -1420, ApiErrorMessage.ARTIFACT_NAME_IS_NULL),
    ARTIFACT_NAME_IS_EMPTY(BAD_REQUEST, -1421, ApiErrorMessage.ARTIFACT_NAME_IS_EMPTY),
    START_DATE_IS_NULL(BAD_REQUEST, -1422, ApiErrorMessage.START_DATE_IS_NULL),
    START_DATE_IS_EMPTY(BAD_REQUEST, -1423, ApiErrorMessage.START_DATE_IS_EMPTY),
    TIME_WINDOW_START_IS_NULL(BAD_REQUEST, -1424, ApiErrorMessage.TIME_WINDOW_START_IS_NULL),
    TIME_WINDOW_START_IS_EMPTY(BAD_REQUEST, -1425, ApiErrorMessage.TIME_WINDOW_START_IS_EMPTY),
    TIME_WINDOW_END_IS_NULL(BAD_REQUEST, -1426, ApiErrorMessage.TIME_WINDOW_END_IS_NULL),
    TIME_WINDOW_END_IS_EMPTY(BAD_REQUEST, -1427, ApiErrorMessage.TIME_WINDOW_END_IS_EMPTY),
    ALLOW_USER_POSTPONE_IS_NULL(BAD_REQUEST, -1428, ApiErrorMessage.ALLOW_USER_POSTPONE_IS_NULL),
    MODEL_IS_NULL(BAD_REQUEST, -1429, ApiErrorMessage.MODEL_IS_NULL),
    SERIAL_IS_NULL(BAD_REQUEST, -1430, ApiErrorMessage.SERIAL_IS_NULL),
    SERIAL_IS_EMPTY(BAD_REQUEST, -1431, ApiErrorMessage.SERIAL_IS_EMPTY),
    CAMPAIGN_PROFILE_IS_NULL(BAD_REQUEST, -1432, ApiErrorMessage.CAMPAIGN_PROFILE_IS_NULL),
    CAMPAIGN_PROFILE_IS_EMPTY(BAD_REQUEST, -1433, ApiErrorMessage.CAMPAIGN_PROFILE_IS_EMPTY),
    CAMPAIGN_RULES_IS_NULL(BAD_REQUEST, -1434, ApiErrorMessage.CAMPAIGN_RULES_IS_NULL),
    CAMPAIGN_RULES_IS_EMPTY(BAD_REQUEST, -1435, ApiErrorMessage.CAMPAIGN_RULES_IS_EMPTY),
    CAMPAIGN_DEVICES_IS_NULL(BAD_REQUEST, -1436, ApiErrorMessage.CAMPAIGN_DEVICES_IS_NULL),
    CAMPAIGN_DEVICES_IS_EMPTY(BAD_REQUEST, -1437, ApiErrorMessage.CAMPAIGN_DEVICES_IS_EMPTY),
    ;

    private static final Map<String, FOTACrudErrorCode> bizCodes =
            Collections.unmodifiableMap(Stream.of(values())
                    .collect(Collectors.toMap(FOTACrudErrorCode::getMsg, Function.identity())));
    private final HttpStatus httpStatus;
    private final Integer bizCode;
    private final String msg;

    public Integer findMatchBizCode(final String failMessage) {
        return Optional.ofNullable(bizCodes.get(failMessage)).orElse(FOTA_CRUD_FAIL).getBizCode();
    }

}
