package com.bos.resource.slice.service.fota;

import com.bos.resource.app.device.repository.device.DeviceRepository;
import com.bos.resource.app.fota.model.dto.CampaignRequestDto;
import com.bos.resource.app.fota.model.dto.CampaignRequestDto.Notification.PeriodParams;
import com.bos.resource.app.fota.model.dto.CampaignResponseDto;
import com.bos.resource.app.fota.model.dto.NotificationFirmwareInfoDto;
import com.bos.resource.app.fota.repository.firmware.FirmwareRepository;
import com.bos.resource.app.fota.service.NewArtifactAutoUpdate;
import com.bos.resource.app.fota.service.NewDeployment;
import com.bos.resource.app.fota.service.UpdateNotifier;
import com.bos.resource.app.resourceowner.ResourceOwnerService;
import com.bos.resource.app.resourceowner.model.dto.ResourceOwnerDto;
import com.bos.resource.app.resourceowner.model.entity.ResourceOwner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import static com.bos.resource.app.fota.model.constants.enums.NotificationType.NEW_ARTIFACT_AUTO_UPDATE;
import static com.bos.resource.app.fota.model.constants.enums.PackageType.FULL;
import static com.bos.resource.app.fota.model.constants.enums.PackageType.INCREMENTAL;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@DisplayName("notification: NewArtifactAutoUpdate Test")
public class UpdateNotifierTest {

    private final ResourceOwnerService resourceOwnerService = mock(ResourceOwnerService.class);
    private final FirmwareRepository firmwareRepository = mock(FirmwareRepository.class);
    private final DeviceRepository deviceRepository = mock(DeviceRepository.class);

    private final ResourceOwnerDto resourceOwnerDto = new ResourceOwnerDto(
            ResourceOwner.builder()
                    .id(1L)
                    .resourceOwnerId("fake_resource_owner_id")
                    .email("fake_email")
                    .companyId(1L)
                    .build()
    );

    @Nested
    @DisplayName("new artifact autoUpdate notify test")
    class NewArtifactAutoUpdateNotifyTest {

        UpdateNotifier newArtifactAutoUpdate = new NewArtifactAutoUpdate(resourceOwnerService, firmwareRepository, deviceRepository);

        private final CampaignRequestDto.Notification notification = new CampaignRequestDto.Notification(
                NEW_ARTIFACT_AUTO_UPDATE.getName(),
                new PeriodParams(
                        LocalDateTime.parse("2023-01-01T00:00:00"),
                        LocalDateTime.parse("2025-12-31T23:59:59")
                ),
                0,
                10
        );

        @Test
        @DisplayName("200 OK")
        void notifyTest() {
            ResourceOwner resourceOwner1 = ResourceOwner.builder()
                    .id(1L)
                    .resourceOwnerId(resourceOwnerDto.getResourceOwnerId())
                    .email(resourceOwnerDto.getEmail())
                    .companyId(resourceOwnerDto.getCompanyId())
                    .build();

            ResourceOwner resourceOwner2 = ResourceOwner.builder()
                    .id(2L)
                    .resourceOwnerId(resourceOwnerDto.getResourceOwnerId())
                    .email(resourceOwnerDto.getEmail() + 2)
                    .companyId(resourceOwnerDto.getCompanyId())
                    .build();


            List<String> modelNames = List.of("EF501", "HF550");
            HashSet<String> modelSets = new HashSet<>(modelNames);

            NotificationFirmwareInfoDto ef501 = new NotificationFirmwareInfoDto(
                    "EF501",
                    "20230102_R1.00",
                    FULL,
                    LocalDateTime.now()
            );

            NotificationFirmwareInfoDto hf550 = new NotificationFirmwareInfoDto(
                    "HF550",
                    "20230415_R1.00",
                    INCREMENTAL,
                    LocalDateTime.now()
            );

            given(resourceOwnerService.findByCompanyId(resourceOwnerDto.getCompanyId())).willReturn(List.of(resourceOwner1, resourceOwner2));
            given(deviceRepository.findModelNameAndFirmwareVersionByUser(resourceOwner1.getResourceOwnerId())).willReturn(modelNames);
            PageRequest pageable = PageRequest.of(notification.offset(), notification.limit());
            PageImpl<NotificationFirmwareInfoDto> notificationFirmwareInfos = new PageImpl<>(List.of(ef501, hf550));
            given(firmwareRepository.findUpdatableFirmwareByCompanyIdAndBetweenRegisteredDate(modelSets, resourceOwnerDto.getCompanyId(), notification, pageable)).willReturn(notificationFirmwareInfos);

            CampaignResponseDto.CreatedNotification notify = newArtifactAutoUpdate.notify(resourceOwnerDto, notification);

            assertThat(notify).isNotNull();
            assertThat(notify.getNotifications()).isNotEmpty();
            assertThat(notify.getNotifications()).hasSize(2);
            assertThat(notify.getNotifications().stream().map(e -> e.getValue().getModel()).collect(toList())).containsExactlyInAnyOrder(ef501.modelName(), hf550.modelName());
            assertThat(notify.getNotifications().stream().map(e -> e.getValue().getArtifactName()).collect(toList())).containsExactlyInAnyOrder(ef501.firmwareVersion(), hf550.firmwareVersion());

            verify(resourceOwnerService, times(1)).findByCompanyId(resourceOwnerDto.getCompanyId());
            verify(deviceRepository, times(2)).findModelNameAndFirmwareVersionByUser(resourceOwner1.getResourceOwnerId());
            verify(firmwareRepository, times(1)).findUpdatableFirmwareByCompanyIdAndBetweenRegisteredDate(
                    modelSets, resourceOwnerDto.getCompanyId(), notification, pageable
            );

        }

    }

    @Nested
    @DisplayName("new deployment notify test")
    class NewDeploymentNotifyTest {

        @Test
        @DisplayName("200 OK")
        void notifyTest() {
            UpdateNotifier newDeployment = new NewDeployment();
            CampaignResponseDto.CreatedNotification notify = newDeployment.notify(resourceOwnerDto, null);
            assertThat(notify).isNull();
        }

    }

}
