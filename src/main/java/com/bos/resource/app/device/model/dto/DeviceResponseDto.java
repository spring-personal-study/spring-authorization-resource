package com.bos.resource.app.device.model.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class DeviceResponseDto {
    private final String message;
    private final Integer code;
    private final List<DeviceDto> assets;

    public static DeviceResponseDto wrapper(List<DeviceDto> deviceDtos) {
        return new DeviceResponseDto("success", 200, deviceDtos);
    }

    @Getter
    @RequiredArgsConstructor
    public static class DeviceDto {
        private final String model;
        private final String serialNumber;
    }
}