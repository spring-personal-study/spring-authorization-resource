package com.bos.resource.app.fota.repository.firmware.querydsl;

import com.bos.resource.app.fota.model.entity.Firmware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QFirmwareRepository {
    Page<Firmware> findByModelPaging(String model, String version, Pageable pageable);
}
