package com.bos.resource.app.resourceowner;

import com.bos.resource.app.resourceowner.exception.ResourceOwnerErrorCode;
import com.bos.resource.app.resourceowner.model.dto.ResourceOwnerDto;
import com.bos.resource.app.resourceowner.model.entity.ResourceOwner;
import com.bos.resource.app.resourceowner.repository.ResourceOwnerRepository;
import com.bos.resource.exception.common.BizException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceOwnerService {

    private final ResourceOwnerRepository resourceOwnerRepository;

    public ResourceOwnerDto findByResourceOwnerId(String resourceOwnerId) {
        ResourceOwner resourceOwner = resourceOwnerRepository.findByResourceOwnerId(resourceOwnerId)
                .orElseThrow(() -> new BizException(ResourceOwnerErrorCode.RESOURCE_OWNER_NOT_FOUND));
        return new ResourceOwnerDto(resourceOwner);
    }

    public List<ResourceOwner> findByCompanyId(Long companyId) {
        return resourceOwnerRepository.findByCompanyId(companyId);
    }
}
