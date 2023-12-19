package com.bos.resource.app.fota.service;

import com.bos.resource.app.fota.model.dto.CampaignRequestDto;
import com.bos.resource.app.fota.model.dto.CampaignResponseDto;
import com.bos.resource.app.resourceowner.model.dto.ResourceOwnerDto;
import org.springframework.data.domain.Pageable;

public interface Notifier {
    CampaignResponseDto.CreatedNotification createCampaign(ResourceOwnerDto requestUser, CampaignRequestDto.Notification notification, Pageable pageable);
}
