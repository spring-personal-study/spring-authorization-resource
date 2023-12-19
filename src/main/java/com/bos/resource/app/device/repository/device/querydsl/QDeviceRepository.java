package com.bos.resource.app.device.repository.device.querydsl;

import com.bos.resource.app.device.model.dto.DeviceResponseDto;

import java.util.List;

public interface QDeviceRepository {
    List<DeviceResponseDto.DeviceDto> findAssetByEnrolledUser(Long userId);
}
