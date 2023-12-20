package com.bos.resource.app.fota.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    NEW_ARTIFACT_AUTO_UPDATE("NEW_ARTIFACT_AUTO_UPDATE"),
    NEW_DEPLOYMENT("NEW_DEPLOYMENT"),
;
    private final String name;

    public static NotificationType from(String name) {
        for (NotificationType notificationType : NotificationType.values()) {
            if (notificationType.name.equals(name)) {
                return notificationType;
            }
        }
        throw new IllegalArgumentException("No NotificationType with name " + name);
    }

    public static String findType(String notificationType) {
        for (NotificationType type : NotificationType.values()) {
            if (type.name.equals(notificationType)) {
                return type.name;
            }
        }
        throw new IllegalArgumentException("No NotificationType with name: {} " + notificationType);
    }
}
