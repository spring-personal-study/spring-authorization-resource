package com.bos.resource.exception.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiErrorMessage {

    public static final String FOTA_CRUD_FAIL = "Request relates with FOTA has failed.";
    public static final String FIRMWARE_NOT_FOUND = "Firmware not found";
    public static final String INCREMENTAL_FIRMWARE_NOT_FOUND = "Incremental firmware not found";
    public static final String PACKAGE_NOT_FOUND = "Package not found";
    public static final String SUPPORT_MODEL_NOT_FOUND = "Support model not found";
    public static final String CAMPAIGN_NOT_FOUND = "not found active deployment.";
    public static final String ATTEMPTED_CANCEL_CAMPAIGN_WITH_NOT_VALID_USER = "not found active deployment. please check deployment id again.";
    public static final String INSERT_JSON_DATA_FAIL = "fail to create deployment.";

    public static final String NOTIFICATION_TYPE_IS_NULL = "notification type is null.";
    public static final String NOTIFICATION_TYPE_IS_EMPTY = "notification type has empty value.";
    public static final String FOTA_READY_IS_NULL = "fotaReady is null.";
    public static final String FOTA_READY_IS_EMPTY = "fotaReady has empty value.";
    public static final String DETAIL_LEVEL_IS_NULL = "detailLevel is null.";
    public static final String DETAIL_LEVEL_IS_EMPTY = "detailLevel has empty value.";
    public static final String DEPLOYMENT_ID_IS_NULL = "deploymentId is null.";
    public static final String DEPLOYMENT_ID_IS_EMPTY = "deploymentId has empty value.";
    public static final String APPEND_STATUS_IS_NULL = "appendStatus is null.";
    public static final String UPDATE_TYPE_IS_NULL = "updateType is null.";
    public static final String UPDATE_TYPE_IS_EMPTY = "updateType has empty value.";
    public static final String ARTIFACT_NAME_IS_NULL = "artifactName is null.";
    public static final String ARTIFACT_NAME_IS_EMPTY = "artifactName has empty value.";
    public static final String START_DATE_IS_NULL = "startDate is null.";
    public static final String START_DATE_IS_EMPTY = "startDate has empty value.";
    public static final String TIME_WINDOW_START_IS_NULL = "timeWindowStart is null.";
    public static final String TIME_WINDOW_START_IS_EMPTY = "timeWindowStart has empty value.";
    public static final String TIME_WINDOW_END_IS_NULL = "timeWindowEnd is null.";
    public static final String TIME_WINDOW_END_IS_EMPTY = "timeWindowEnd has empty value.";
    public static final String ALLOW_USER_POSTPONE_IS_NULL = "allowUserPostpone is null.";
    public static final String MODEL_IS_NULL = "model is null.";
    public static final String SERIAL_IS_NULL = "serial is null.";
    public static final String SERIAL_IS_EMPTY = "serial has empty value.";

    public static final String RESOURCE_OWNER_CRUD_FAIL = "Request relates with User has failed.";
    public static final String RESOURCE_OWNER_NOT_FOUND = "the user cannot found.";
}
