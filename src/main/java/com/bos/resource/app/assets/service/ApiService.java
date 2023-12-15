package com.bos.resource.app.assets.service;

import com.bos.resource.app.assets.model.dto.DeviceResponseDto;
import com.bos.resource.app.assets.model.dto.DeviceResponseDto.DeviceDto;
import com.bos.resource.app.assets.repository.DeviceRepository;
import com.bos.resource.app.resourceowner.model.entity.ResourceOwner;
import com.bos.resource.app.resourceowner.repository.ResourceOwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApiService {

    private final ResourceOwnerRepository resourceOwnerRepository;
    private final DeviceRepository deviceRepository;

    public DeviceResponseDto findAssetByEnrolledUser(String username) {
       ResourceOwner resourceOwner = resourceOwnerRepository.findByResourceOwnerId(username);
       List<DeviceDto> devices = deviceRepository.findAssetByEnrolledUser(resourceOwner.getId());
       return DeviceResponseDto.wrapper(devices);
    }
}
