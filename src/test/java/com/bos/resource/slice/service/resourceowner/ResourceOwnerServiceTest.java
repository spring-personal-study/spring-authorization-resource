package com.bos.resource.slice.service.resourceowner;

import com.bos.resource.app.resourceowner.ResourceOwnerService;
import com.bos.resource.app.resourceowner.exception.ResourceOwnerErrorCode;
import com.bos.resource.app.resourceowner.model.dto.ResourceOwnerDto;
import com.bos.resource.app.resourceowner.model.entity.ResourceOwner;
import com.bos.resource.app.resourceowner.repository.ResourceOwnerRepository;
import com.bos.resource.exception.common.BizException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.verify;

@DisplayName("Unit Test - ResourceOwner Service")
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

        private final String resourceOwnerId = "saved_resourceOwnerId";

        @Test
        @DisplayName("200 OK")
        void findByResourceOwnerIdTest200() {
            ResourceOwner resourceOwner = ResourceOwner.builder()
                    .resourceOwnerId(resourceOwnerId)
                    .build();
            given(resourceOwnerRepository.findByResourceOwnerId(resourceOwnerId))
                    .willReturn(Optional.of(resourceOwner));

            ResourceOwnerDto resourceOwnerDto = resourceOwnerService.findByResourceOwnerId(resourceOwnerId);

            assertThat(resourceOwnerDto).isNotNull();
            assertThat(resourceOwnerDto.getResourceOwnerId()).isEqualTo(resourceOwner.getResourceOwnerId());

            verify(resourceOwnerRepository, atMostOnce()).findByResourceOwnerId(resourceOwnerId);
        }

        @Test
        @DisplayName("404 Not Found")
        void findByResourceOwnerIdTest404() {
            ResourceOwner resourceOwner = ResourceOwner.builder()
                    .resourceOwnerId(resourceOwnerId)
                    .build();
            given(resourceOwnerRepository.findByResourceOwnerId(resourceOwnerId))
                    .willThrow(new BizException(ResourceOwnerErrorCode.RESOURCE_OWNER_NOT_FOUND));

            assertThatThrownBy(() -> resourceOwnerService.findByResourceOwnerId(resourceOwnerId))
                    .isInstanceOf(BizException.class)
                    .hasMessageContaining(ResourceOwnerErrorCode.RESOURCE_OWNER_NOT_FOUND.getMsg());

            verify(resourceOwnerRepository, atMostOnce()).findByResourceOwnerId(resourceOwnerId);
        }

    }

    @Nested
    @DisplayName("findByCompanyId test")
    class findByCompanyIdTest {

        private final Long companyId = 1L;

        @Test
        @DisplayName("200 OK")
        void findByCompanyIdTest200() {
            ResourceOwner resourceOwner1 = ResourceOwner.builder()
                    .id(1L)
                    .resourceOwnerId("saved_resourceOwnerId_1")
                    .companyId(companyId)
                    .build();

            ResourceOwner resourceOwner2 = ResourceOwner.builder()
                    .id(2L)
                    .resourceOwnerId("saved_resourceOwnerId_2")
                    .companyId(companyId)
                    .build();

            given(resourceOwnerRepository.findByCompanyId(companyId))
                    .willReturn(List.of(resourceOwner1, resourceOwner2));

            List<ResourceOwner> resourceOwners = resourceOwnerService.findByCompanyId(companyId);

            assertThat(resourceOwners).isNotEmpty();
            assertThat(resourceOwners).allSatisfy(resourceOwner -> {
                assertThat(resourceOwner.getId()).isNotNull();
                assertThat(resourceOwner.getResourceOwnerId()).isNotNull();
                assertThat(resourceOwner.getCompanyId()).isEqualTo(companyId);
            });

            verify(resourceOwnerRepository, atMostOnce()).findByCompanyId(companyId);
        }

        @Test
        @DisplayName("404 Not Found")
        void findByCompanyIdTest404() {
            given(resourceOwnerRepository.findByCompanyId(companyId))
                    .willReturn(Collections.emptyList());

            List<ResourceOwner> notFounds = resourceOwnerRepository.findByCompanyId(companyId);
            assertThat(notFounds).isEmpty();

            verify(resourceOwnerRepository, atMostOnce()).findByCompanyId(companyId);
        }
    }
}
