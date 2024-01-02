package com.bos.resource.app.fota.repository.devicemap;

import com.bos.resource.app.fota.model.entity.Campaign;
import com.bos.resource.app.fota.model.entity.CampaignDeviceMap;
import com.bos.resource.app.fota.repository.devicemap.querydsl.QCampaignDeviceMapRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignDeviceMapRepository extends JpaRepository<CampaignDeviceMap, Long>, QCampaignDeviceMapRepository {
    void deleteByCampaign(Campaign campaign);
}
