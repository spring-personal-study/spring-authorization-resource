package com.bos.resource.app.fota.model.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OperationType {
    CONFIG("CONFIG"), MESSAGE("MESSAGE"), INFO("INFO"), COMMAND("COMMAND"),
    PROFILE("PROFILE"), POLICY("POLICY"), PRODUCT("PRODUCT"),MCM("MCM"),
    REPORT("REPORT");

    private final String value;

}
