package com.bos.resource.app.fota.service.updatetype;

import com.bos.resource.app.fota.model.dto.CampaignRequestDto;
import com.bos.resource.app.fota.model.dto.CampaignResponseDto.CreatedCampaign;
import com.bos.resource.app.resourceowner.model.dto.ResourceOwnerDto;

public interface UpdateTypeSelector {
    CreatedCampaign createCampaign(ResourceOwnerDto requestUser, CampaignRequestDto.CreateCampaignDto createCampaignDto);
}
