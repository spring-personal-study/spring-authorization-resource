package com.bos.resource.slice.service;

import com.bos.resource.app.resourceowner.ResourceOwnerService;
import com.bos.resource.app.resourceowner.model.entity.ResourceOwner;
import com.bos.resource.app.resourceowner.repository.ResourceOwnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.mockito.BDDMockito.given;

@DisplayName("Resource Owner Service Test")
public class ResourceOwnerServiceTest {

    private ResourceOwnerRepository resourceOwnerRepository;
    private ResourceOwnerService resourceOwnerService;

    @BeforeEach
    void setUp() {
        resourceOwnerRepository = Mockito.mock(ResourceOwnerRepository.class);
        resourceOwnerService = new ResourceOwnerService(resourceOwnerRepository);
    }

    @Nested
    @DisplayName("findByResourceOwnerId test")
    class findByResourceOwnerIdTest {

        private final String resourceOwnerId = "resourceOwnerId";

        @Test
        @DisplayName("200 OK")
        void findByResourceOwnerIdTest200() {
            ResourceOwner resourceOwner = ResourceOwner.builder()
                    .resourceOwnerId(resourceOwnerId)
                    .build();
            given(resourceOwnerRepository.findByResourceOwnerId(resourceOwnerId))
                    .willReturn(Optional.of(resourceOwner));

        }

    }

    @Nested
    @DisplayName("findByCompanyId test")
    class findByCompanyIdTest {

    }
}
