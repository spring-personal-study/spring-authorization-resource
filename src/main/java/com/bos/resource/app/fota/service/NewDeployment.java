package com.bos.resource.app.fota.service;

import com.bos.resource.app.fota.model.dto.CampaignRequestDto;
import com.bos.resource.app.fota.model.dto.CampaignResponseDto;
import com.bos.resource.app.resourceowner.model.dto.ResourceOwnerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("NEW_DEPLOYMENT")
@RequiredArgsConstructor
public class NewDeployment implements UpdateNotifier {

  /*  private final CampaignRepository campaignRepository;
    private final FirmwareRepository firmwareRepository;
    private final PackageRepository packageRepository;
    private final SupportModelRepository supportModelRepository;
    private final CampaignPackageMapRepository campaignPackageMapRepository;
    private final DeviceRepository deviceRepository;
    private final CampaignDeviceMapRepository campaignDeviceMapRepository;
    private final CampaignDeviceTagMapRepository campaignDeviceTagMapRepository;
    private final CampaignDeviceGroupMapRepository campaignDeviceGroupMapRepository;
    private final DeviceTagMapRepository deviceTagMapRepository;
    private final DeviceGroupMapRepository deviceGroupMapRepository;*/

    @Override
    public CampaignResponseDto.CreatedNotification notify(ResourceOwnerDto requestUser, CampaignRequestDto.Notification notification) {
        return null;
    }

}
