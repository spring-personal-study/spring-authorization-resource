package com.bos.resource.app.fota.repository.firmware.querydsl;

import com.bos.resource.app.fota.model.entity.Firmware;

import java.util.List;

public interface QFirmwareRepository {
    //Page<Firmware> findByModelPaging(String model, String version, Pageable pageable);
    List<Firmware> findByModelAndVersion(String model, String version);
    Firmware findOneLatestByModel(String model);
}
