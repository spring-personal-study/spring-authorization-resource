package com.bos.resource.app.assets.repository.querydsl;

import com.bos.resource.app.assets.model.dto.DeviceResponseDto;

import java.util.List;

public interface QDeviceRepository {
    List<DeviceResponseDto.DeviceDto> findAssetByEnrolledUser(Long userId);
}
