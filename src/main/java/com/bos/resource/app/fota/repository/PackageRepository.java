package com.bos.resource.app.fota.repository;

import com.bos.resource.app.device.model.entity.SupportModel;
import com.bos.resource.app.fota.model.entity.Firmware;
import com.bos.resource.app.fota.model.entity.Package;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackageRepository extends JpaRepository<Package, Long> {
    Package findByFirmwareAndModelAndTargetVersion(Firmware firmware, SupportModel model, String version);
    Package findByFirmwareAndModel(Firmware firmware, SupportModel supportModel);
}
