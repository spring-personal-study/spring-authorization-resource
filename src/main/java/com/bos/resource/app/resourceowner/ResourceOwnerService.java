package com.bos.resource.app.resourceowner;

import com.bos.resource.app.resourceowner.model.dto.ResourceOwnerDto;
import com.bos.resource.app.resourceowner.model.entity.ResourceOwner;
import com.bos.resource.app.resourceowner.repository.ResourceOwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResourceOwnerService {

    private final ResourceOwnerRepository resourceOwnerRepository;

    public ResourceOwnerDto findByResourceOwnerId(String resourceOwnerId) {
        ResourceOwner resourceOwner = resourceOwnerRepository.findByResourceOwnerId(resourceOwnerId);
        return new ResourceOwnerDto(resourceOwner);
    }
}
