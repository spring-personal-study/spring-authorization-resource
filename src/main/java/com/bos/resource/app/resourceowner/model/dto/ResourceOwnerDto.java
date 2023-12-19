package com.bos.resource.app.resourceowner.model.dto;

import com.bos.resource.app.resourceowner.model.entity.ResourceOwner;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ResourceOwnerDto {
    private final Long id;
    private final String resourceOwnerId;
    private final String email;
    private final Long companyId;

    public ResourceOwnerDto(ResourceOwner resourceOwner) {
        this.id = resourceOwner.getId();
        this.resourceOwnerId = resourceOwner.getResourceOwnerId();
        this.email = resourceOwner.getEmail();
        this.companyId = resourceOwner.getCompanyId();
    }
}
