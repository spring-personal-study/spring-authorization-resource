package com.bos.resource.app.fota.service.updatetype;

import com.bos.resource.app.device.model.entity.SupportModel;
import com.bos.resource.app.fota.exception.FOTACrudErrorCode;
import com.bos.resource.app.fota.model.dto.CampaignRequestDto;
import com.bos.resource.app.fota.model.dto.CampaignRequestDto.CreateCampaignDto.CampaignProfile;
import com.bos.resource.app.fota.model.dto.CampaignResponseDto.CreatedCampaign;
import com.bos.resource.app.fota.model.entity.Campaign;
import com.bos.resource.app.fota.model.entity.Firmware;
import com.bos.resource.app.fota.model.entity.Package;
import com.bos.resource.app.fota.repository.CampaignRepository;
import com.bos.resource.app.fota.repository.PackageRepository;
import com.bos.resource.app.fota.repository.firmware.FirmwareRepository;
import com.bos.resource.app.fota.service.updatetype.UpdateTypeHelper.CreateCampaignIngredients;
import com.bos.resource.app.resourceowner.model.dto.ResourceOwnerDto;
import com.bos.resource.exception.common.BizException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.bos.resource.app.fota.model.constants.strings.FirmwareUpdateTypeConstants.CUSTOM_VALUE;

@Service(CUSTOM_VALUE)
@RequiredArgsConstructor
public class UpdateTypeCustom implements UpdateTypeSelector {

    private final CampaignRepository campaignRepository;
    private final PackageRepository packageRepository;
    private final FirmwareRepository firmwareRepository;
    private final UpdateTypeHelper createCampaignKit;

    @Transactional
    @Override
    public CreatedCampaign createCampaign(ResourceOwnerDto requestUser, CampaignRequestDto.CreateCampaignDto createCampaignDto) {
        CreateCampaignIngredients newCampaign = createCampaignKit.prepareToSave(requestUser, createCampaignDto);
        if (doesNotReceivedFirmwareVersionForCustom(createCampaignDto)) {
            throw new BizException(FOTACrudErrorCode.ARTIFACT_NAME_IS_NULL);
        }
        List<Firmware> firmwares = firmwareRepository.findByModelAndVersion(createCampaignDto.devices().model(), createCampaignDto.profile().target().value().artifactName());
        if (firmwares.isEmpty()) throw new BizException(FOTACrudErrorCode.FIRMWARE_NOT_FOUND);
        savePackageIfDoesNotExists(requestUser, createCampaignDto, firmwares, newCampaign.supportModel(), 1);
        Campaign savedCampaign = campaignRepository.save(newCampaign.newCampaign());

        // record lists for campaign details result
        //List<String> expiredWarranty = new ArrayList<>(), notFound = new ArrayList<>();

        // save campaign details
        for (Firmware firmware : firmwares) {
            // TODO: need to consider about the package types. does it need to receive FULL/INCREMENTAL option as additional parameters from the user?
            if ( //PackageType.INCREMENTAL.equals(firmware.getPackageType()) &&
                    createCampaignDto.profile().target().value().artifactName().equals(firmware.getVersion())
            ) {
                Package targetPackage = packageRepository.findByFirmwareAndModelAndTargetVersion(firmware, newCampaign.supportModel(), createCampaignDto.profile().target().value().artifactName());
                createCampaignKit.saveCampaignDetails(savedCampaign, targetPackage, createCampaignDto.devices().serial());
                //createCampaignKit.saveCampaignDetails(savedCampaign, targetPackage, createCampaignDto.devices().serial(), notFound, expiredWarranty);
            }
        }
        return CreatedCampaign.builder()
                .deploymentId(savedCampaign.getName())
                .action("created")
                .message("create deployment success")
                .code("200")
                .timestamp(LocalDateTime.now())
                .build();
    }

    private boolean doesNotReceivedFirmwareVersionForCustom(CampaignRequestDto.CreateCampaignDto createCampaignDto) {
        CampaignProfile profile = createCampaignDto.profile();
        return profile.target() == null ||
                profile.target().value() == null ||
                profile.target().value().artifactName() == null;
    }

    private void savePackageIfDoesNotExists(ResourceOwnerDto requestUser, CampaignRequestDto.CreateCampaignDto createCampaignDto, List<Firmware> firmwares, SupportModel supportModel, int seq) {
        for (Firmware firmware : firmwares) {
            Package targetPackage = packageRepository.findByFirmwareAndModelAndTargetVersion(firmware, supportModel, createCampaignDto.profile().target().value().artifactName());
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
