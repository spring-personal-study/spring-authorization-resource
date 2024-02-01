package com.bos.resource.app.fota.model.dto;

import com.bos.resource.app.fota.model.constants.enums.PackageType;

import java.time.LocalDateTime;

public record NotificationFirmwareInfoDto(
        String modelName,
        String firmwareVersion,
        PackageType packageType,
        LocalDateTime createDt
) {
}
