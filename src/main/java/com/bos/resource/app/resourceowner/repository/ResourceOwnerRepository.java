package com.bos.resource.app.resourceowner.repository;

import com.bos.resource.app.resourceowner.model.entity.ResourceOwner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceOwnerRepository extends JpaRepository<ResourceOwner, Long> {
    ResourceOwner findByResourceOwnerId(String username);
}
