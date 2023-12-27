package com.bos.resource.app.fota.repository;

import com.bos.resource.app.fota.model.entity.Campaign;
import com.bos.resource.app.fota.model.entity.CampaignPackageMap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignPackageMapRepository extends JpaRepository<CampaignPackageMap, Long>  {
    void deleteByCampaign(Campaign campaign);
}
