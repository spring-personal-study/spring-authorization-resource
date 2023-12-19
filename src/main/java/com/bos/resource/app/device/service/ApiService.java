package com.bos.resource.app.device.service;

import com.bos.resource.app.device.model.dto.DeviceResponseDto;
import com.bos.resource.app.device.model.dto.DeviceResponseDto.DeviceDto;
import com.bos.resource.app.device.repository.device.DeviceRepository;
import com.bos.resource.app.resourceowner.ResourceOwnerService;
import com.bos.resource.app.resourceowner.model.dto.ResourceOwnerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApiService {

    private final ResourceOwnerService resourceOwnerService;
    private final DeviceRepository deviceRepository;

    public DeviceResponseDto findAssetByEnrolledUser(String username) {
       ResourceOwnerDto resourceOwner = resourceOwnerService.findByResourceOwnerId(username);
       List<DeviceDto> devices = deviceRepository.findAssetByEnrolledUser(resourceOwner.getId());
       return DeviceResponseDto.wrapper(devices);
    }
}
