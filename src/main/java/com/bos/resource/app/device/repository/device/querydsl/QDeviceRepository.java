package com.bos.resource.app.device.repository.device.querydsl;

import com.bos.resource.app.device.model.dto.DeviceResponseDto;
import com.bos.resource.app.fota.model.dto.CampaignRequestDto.FOTAReadyDevice;
import com.bos.resource.app.fota.model.dto.CampaignResponseDto.FotaReadyDevice.FOTAReadyDeviceContent;
import com.bos.resource.app.fota.model.dto.NotificationFirmwareInfoDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface QDeviceRepository {
    List<DeviceResponseDto.DeviceDto> findAssetByEnrolledUser(Long userId);
    Page<FOTAReadyDeviceContent> findFOTAReadyDevice(Long companyId, FOTAReadyDevice campaignDevice);
    List<String> findModelNameAndFirmwareVersionByUser(String resourceOwnerId);
}
