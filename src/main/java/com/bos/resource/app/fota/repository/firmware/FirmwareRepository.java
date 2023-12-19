package com.bos.resource.app.fota.repository.firmware;

import com.bos.resource.app.fota.model.entity.Firmware;
import com.bos.resource.app.fota.repository.firmware.querydsl.QFirmwareRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FirmwareRepository extends JpaRepository<Firmware, Long>, QFirmwareRepository {
}
