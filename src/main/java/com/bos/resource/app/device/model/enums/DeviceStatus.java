package com.bos.resource.app.device.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeviceStatus {

    CREATED(1),
    ENROLL(2),
    ACTIVE(3),
    INACTIVE(4),
    UNREACHABLE(5);

    private final int id;
}
