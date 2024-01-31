package com.bos.resource.app.fota.service;

import com.bos.resource.app.device.repository.device.DeviceRepository;
import com.bos.resource.app.fota.model.dto.CampaignRequestDto;
import com.bos.resource.app.fota.model.dto.CampaignResponseDto;
import com.bos.resource.app.fota.model.dto.NotificationFirmwareInfoDto;
import com.bos.resource.app.fota.model.enums.NotificationType;
import com.bos.resource.app.fota.repository.firmware.FirmwareRepository;
import com.bos.resource.app.resourceowner.ResourceOwnerService;
import com.bos.resource.app.resourceowner.model.dto.ResourceOwnerDto;
import com.bos.resource.app.resourceowner.model.entity.ResourceOwner;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.bos.resource.app.fota.model.dto.NotificationTypeConstants.NEW_ARTIFACT_AUTO_UPDATE_VALUE;

@Service(NEW_ARTIFACT_AUTO_UPDATE_VALUE)
@RequiredArgsConstructor
public class NewArtifactAutoUpdate implements UpdateNotifier {

    private final FirmwareRepository firmwareRepository;
    private final ResourceOwnerService resourceOwnerService;
    private final DeviceRepository deviceRepository;

    @Override
    public CampaignResponseDto.CreatedNotification notify(
            ResourceOwnerDto requestUser,
            CampaignRequestDto.Notification notification
    ) {
        List<UpdatableFirmwareVersion> updatableFirmwareVersions = new ArrayList<>();

        List<ResourceOwner> users = resourceOwnerService.findByCompanyId(requestUser.getCompanyId());
        Set<String> modelNames = new HashSet<>();
        for (ResourceOwner user : users) {
            List<String> models = deviceRepository.findModelNameAndFirmwareVersionByUser(user.getResourceOwnerId());
            modelNames.addAll(models);
        }
        PageRequest pageable = PageRequest.of(notification.offset(), notification.limit());
        Page<NotificationFirmwareInfoDto> updatable = firmwareRepository.findUpdatableFirmwareByCompanyIdAndBetweenRegisteredDate(modelNames, requestUser.getCompanyId(), notification, pageable);
        return CampaignResponseDto.CreatedNotification.of(
                NotificationType.from(notification.notificationType()),
                updatable,
                pageable
        );
    }

    @Builder
    public static class UpdatableFirmwareVersion {
        private final String modelName;
        private final List<String> updatableFirmwareVersions;

        UpdatableFirmwareVersion(String modelName, List<String> updatable) {
            this.modelName = modelName;
            this.updatableFirmwareVersions = updatable;

        }
    }
}
