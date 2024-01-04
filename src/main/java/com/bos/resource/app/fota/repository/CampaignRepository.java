package com.bos.resource.app.fota.repository;

import com.bos.resource.app.fota.model.entity.Campaign;
import com.bos.resource.app.fota.model.entity.Firmware;
import com.bos.resource.app.fota.model.entity.Package;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CampaignRepository extends JpaRepository<Campaign, Long>, QCampaignRepository {

    Optional<Campaign> findByName(String name);
}
