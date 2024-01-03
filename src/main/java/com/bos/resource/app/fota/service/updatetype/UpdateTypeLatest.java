package com.bos.resource.app.fota.service.updatetype;

import com.bos.resource.app.device.model.entity.SupportModel;
import com.bos.resource.app.device.repository.device.DeviceRepository;
import com.bos.resource.app.fota.exception.FOTACrudErrorCode;
import com.bos.resource.app.fota.model.dto.CampaignRequestDto;
import com.bos.resource.app.fota.model.dto.CampaignResponseDto.CreatedCampaign;
import com.bos.resource.app.fota.model.entity.Campaign;
import com.bos.resource.app.fota.model.entity.Firmware;
import com.bos.resource.app.fota.model.entity.Package;
import com.bos.resource.app.fota.repository.CampaignPackageMapRepository;
import com.bos.resource.app.fota.repository.CampaignRepository;
import com.bos.resource.app.fota.repository.PackageRepository;
import com.bos.resource.app.fota.repository.firmware.FirmwareRepository;
import com.bos.resource.app.resourceowner.model.dto.ResourceOwnerDto;
import com.bos.resource.exception.common.BizException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;

@Service("LATEST")
@RequiredArgsConstructor
public class UpdateTypeLatest implements UpdateTypeSelector {

    private final CampaignRepository campaignRepository;
    private final PackageRepository packageRepository;
    private final DeviceRepository deviceRepository;
    private final CampaignPackageMapRepository campaignPackageMapRepository;
    private final FirmwareRepository firmwareRepository;
    private final UpdateTypeHelper createCampaignKit;

    @Transactional
    @Override
    public CreatedCampaign createCampaign(ResourceOwnerDto requestUser, CampaignRequestDto.CreateCampaignDto createCampaignDto) {
        UpdateTypeHelper.CreateCampaignIngredients newCampaign = createCampaignKit.prepareToSave(requestUser, createCampaignDto);
        Firmware firmware = firmwareRepository.findOneLatestByModel(createCampaignDto.devices().model());
        if (firmware == null) throw new BizException(FOTACrudErrorCode.FIRMWARE_NOT_FOUND);
        savePackageIfDoesNotExists(requestUser, singletonList(firmware), newCampaign.supportModel(), 1);
        Campaign savedCampaign = campaignRepository.save(newCampaign.newCampaign());

        // record lists for campaign details result
        List<String> expiredWarranty = new ArrayList<>(), notFound = new ArrayList<>();

        Package targetPackage = packageRepository.findByFirmwareAndModel(firmware, newCampaign.supportModel());
        createCampaignKit.saveCampaignDetails(savedCampaign, targetPackage, createCampaignDto.devices().serial(), notFound, expiredWarranty);

        return CreatedCampaign.builder()
                .deploymentId(savedCampaign.getName())
                .action("created")
                .message("create deployment success")
                .code("200")
                .timestamp(LocalDateTime.now())
                .build();
    }

    private void savePackageIfDoesNotExists(ResourceOwnerDto requestUser, List<Firmware> firmwares, SupportModel supportModel, int seq) {
        for (Firmware firmware : firmwares) {
            Package targetPackage = packageRepository.findByFirmwareAndModel(firmware, supportModel);
            String packageNamePrefix = firmware.getUploadServerType().getType() + "-" + firmware.getPackageType() + "-PACKAGE-";
            if (targetPackage != null && targetPackage.getPackageName().startsWith(packageNamePrefix)) {
                seq = Integer.parseInt(targetPackage.getPackageName().substring(packageNamePrefix.length())) + 1;
            }
            if (targetPackage == null) {
                Package newPackage = Package.createPackage((packageNamePrefix + (seq++)), firmware, supportModel, requestUser);
                packageRepository.save(newPackage);
            }
        }
    }

}
