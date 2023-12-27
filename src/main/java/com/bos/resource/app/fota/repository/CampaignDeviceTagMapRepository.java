package com.bos.resource.app.fota.repository;

import com.bos.resource.app.fota.model.entity.Campaign;
import com.bos.resource.app.fota.model.entity.CampaignDeviceTagMap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignDeviceTagMapRepository extends JpaRepository<CampaignDeviceTagMap, Long> {
    void deleteByCampaign(Campaign campaign);
}
