package com.bos.resource.app.fota.service;

import com.bos.resource.app.fota.model.dto.CampaignRequestDto.Notification;
import com.bos.resource.app.fota.model.dto.CampaignResponseDto;
import com.bos.resource.app.fota.model.dto.CampaignResponseDto.CreatedNotification;
import com.bos.resource.app.resourceowner.model.dto.ResourceOwnerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FOTAService {

    private final Map<String, Notifier> notificationProcessors;

    public CreatedNotification processNotification(ResourceOwnerDto requestUser, Notification notification, Pageable pageable) {
        Notifier notifier = notificationProcessors.get(notification.notificationType());
        return notifier.createCampaign(requestUser, notification, pageable);
    }

}
