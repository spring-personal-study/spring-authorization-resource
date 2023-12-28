package com.bos.resource.exception.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiErrorMessage {

    public static final String FOTA_CRUD_FAIL = "FOTA CRUD fail";
    public static final String FIRMWARE_NOT_FOUND = "Firmware not found";
    public static final String INCRMENTAL_FIRMWARE_NOT_FOUND = "Incremental firmware not found";
    public static final String PACKAGE_NOT_FOUND = "Package not found";
    public static final String SUPPORT_MODEL_NOT_FOUND = "Support model not found";
    public static final String CAMPAIGN_NOT_FOUND = "not found active deployment. it could be already canceled.";
    public static final String ATTEMPTED_CANCEL_CAMPAIGN_WITH_NOT_VALID_USER = "not found active deployment. please check deployment id again.";
}
