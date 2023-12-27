package com.bos.resource.app.fota.repository;

import com.bos.resource.app.fota.model.entity.Campaign;
import com.bos.resource.app.fota.model.entity.CampaignDeviceGroupMap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignDeviceGroupMapRepository extends JpaRepository<CampaignDeviceGroupMap, Long> {
    void deleteByCampaign(Campaign campaign);
}
