package com.bos.resource.app.fota.repository.firmware.querydsl;

import com.bos.resource.app.fota.model.dto.CampaignRequestDto;
import com.bos.resource.app.fota.model.dto.NotificationFirmwareInfoDto;
import com.bos.resource.app.fota.model.entity.Firmware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface QFirmwareRepository {
    //Page<Firmware> findByModelPaging(String model, String version, Pageable pageable);
    List<Firmware> findByModelAndVersion(String model, String version);

    Firmware findOneLatestByModel(String model);

    Page<NotificationFirmwareInfoDto> findUpdatableFirmwareByCompanyIdAndBetweenRegisteredDate(Set<String> modelName, Long companyId, CampaignRequestDto.Notification notification, Pageable pageable);
}
