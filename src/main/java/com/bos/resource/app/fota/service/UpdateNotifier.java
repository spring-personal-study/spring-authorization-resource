package com.bos.resource.app.fota.service;

import com.bos.resource.app.fota.model.dto.CampaignRequestDto;
import com.bos.resource.app.fota.model.dto.CampaignResponseDto;
import com.bos.resource.app.resourceowner.model.dto.ResourceOwnerDto;

public interface UpdateNotifier {
    CampaignResponseDto.CreatedNotification notify(ResourceOwnerDto requestUser, CampaignRequestDto.Notification notification);
}
