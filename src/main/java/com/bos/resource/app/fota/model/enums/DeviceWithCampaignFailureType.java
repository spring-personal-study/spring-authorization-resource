package com.bos.resource.app.fota.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeviceWithCampaignFailureType {
    EXPIRED_WARRANTY("4001", "the device's warranty has expired."),
    NOT_FOUND("4002", "the device is not found."),
    ;

    private final String errorCode;
    private final String detailMsg;
}
