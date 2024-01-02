package com.bos.resource.app.fota.repository.devicemap.querydsl;

import com.bos.resource.app.fota.model.entity.Campaign;
import com.bos.resource.app.fota.model.entity.CampaignDeviceMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QCampaignDeviceMapRepository {

    Page<CampaignDeviceMap> findByCampaignDevices(Campaign targetCampaign, Pageable pageable);
}
