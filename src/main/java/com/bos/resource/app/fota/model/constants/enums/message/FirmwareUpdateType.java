package com.bos.resource.app.fota.model.constants.enums.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.bos.resource.app.fota.model.constants.strings.FirmwareUpdateTypeConstants.CUSTOM_VALUE;
import static com.bos.resource.app.fota.model.constants.strings.FirmwareUpdateTypeConstants.LATEST_VALUE;

@Getter
@RequiredArgsConstructor
public enum FirmwareUpdateType {
    CUSTOM(CUSTOM_VALUE),
    LATEST(LATEST_VALUE)
    ;

    private final String name;

    public static FirmwareUpdateType from(String name) {
        for (FirmwareUpdateType updateType : FirmwareUpdateType.values()) {
            if (updateType.name.equals(name)) {
                return updateType;
            }
        }
        throw new IllegalArgumentException("No NotificationType with name: {} " + name);
    }

    public static String findType(String updateType) {
        for (FirmwareUpdateType type : FirmwareUpdateType.values()) {
            if (type.name.equals(updateType)) {
                return type.name;
            }
        }
        throw new IllegalArgumentException("No NotificationType with name: {} " + updateType);
    }
}
