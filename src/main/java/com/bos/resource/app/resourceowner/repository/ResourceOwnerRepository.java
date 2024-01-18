package com.bos.resource.app.resourceowner.repository;

import com.bos.resource.app.resourceowner.model.entity.ResourceOwner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResourceOwnerRepository extends JpaRepository<ResourceOwner, Long> {
    Optional<ResourceOwner> findByResourceOwnerId(String username);
}
