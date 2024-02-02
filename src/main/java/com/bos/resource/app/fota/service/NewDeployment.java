package com.bos.resource.app.fota.service;

import com.bos.resource.app.fota.model.dto.CampaignRequestDto;
import com.bos.resource.app.fota.model.dto.CampaignResponseDto;
import com.bos.resource.app.resourceowner.model.dto.ResourceOwnerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.bos.resource.app.fota.model.constants.strings.NotificationTypeConstants.NEW_DEPLOYMENT_VALUE;

@Service(NEW_DEPLOYMENT_VALUE)
@RequiredArgsConstructor
public class NewDeployment implements UpdateNotifier {

    @Override
    public CampaignResponseDto.CreatedNotification notify(ResourceOwnerDto requestUser, CampaignRequestDto.Notification notification) {
        return null;
    }

}
