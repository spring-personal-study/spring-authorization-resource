package com.bos.resource.app.fota.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FirmwareUploadServerType {
    CLOUD("CLOUD"),
    LOCAL("LOCAL"),
    CENTRAL("CENTRAL"),
    NOT_SUPPORT("NOT_SUPPORT");

    private final String type;
}
