package com.bos.resource.app.fota.service;

import com.bos.resource.app.fota.model.dto.CampaignRequestDto;
import com.bos.resource.app.fota.model.dto.CampaignResponseDto;
import com.bos.resource.app.resourceowner.model.dto.ResourceOwnerDto;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service("NEW_ARTIFACT_AUTO_UPDATE")
public class NewArtifactAutoUpdate implements Notifier {

    @Override
    public CampaignResponseDto.CreatedNotification createCampaign(ResourceOwnerDto requestUser, CampaignRequestDto.Notification notification, Pageable pageable) {
        return null;
    }
}
