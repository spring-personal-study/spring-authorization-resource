package com.bos.resource.app.fota.service;

import com.bos.resource.app.common.domain.enums.UseType;
import com.bos.resource.app.device.model.entity.Device;
import com.bos.resource.app.device.model.entity.DeviceGroupMap;
import com.bos.resource.app.device.model.entity.DeviceTagMap;
import com.bos.resource.app.device.model.entity.SupportModel;
import com.bos.resource.app.device.repository.device.DeviceRepository;
import com.bos.resource.app.device.repository.devicegroup.DeviceGroupMapRepository;
import com.bos.resource.app.device.repository.devicetag.DeviceTagMapRepository;
import com.bos.resource.app.fota.exception.FOTACrudErrorCode;
import com.bos.resource.app.fota.model.dto.CampaignRequestDto;
import com.bos.resource.app.fota.model.dto.CampaignRequestDto.Notification;
import com.bos.resource.app.fota.model.dto.CampaignResponseDto.*;
import com.bos.resource.app.fota.model.dto.CampaignResponseDto.FotaReadyDevice.FOTAReadyDeviceContent;
import com.bos.resource.app.fota.model.dto.CampaignStatusAggregation;
import com.bos.resource.app.fota.model.dto.ConvertedDateString;
import com.bos.resource.app.fota.model.entity.Package;
import com.bos.resource.app.fota.model.entity.*;
import com.bos.resource.app.fota.model.enums.PackageType;
import com.bos.resource.app.fota.repository.*;
import com.bos.resource.app.fota.repository.devicemap.CampaignDeviceMapRepository;
import com.bos.resource.app.fota.repository.firmware.FirmwareRepository;
import com.bos.resource.app.resourceowner.ResourceOwnerService;
import com.bos.resource.app.resourceowner.model.dto.ResourceOwnerDto;
import com.bos.resource.exception.common.BizException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Transactional(readOnly = true)
@Slf4j
@Service
@RequiredArgsConstructor
public class FOTAService {

    private final Map<String, Notifier> notificationProcessors;
    private final CampaignRepository campaignRepository;
    private final DeviceRepository deviceRepository;
    private final CampaignDeviceMapRepository campaignDeviceMapRepository;
    private final CampaignPackageMapRepository campaignPackageMapRepository;
    private final CampaignDeviceGroupMapRepository campaignDeviceGroupMapRepository;
    private final CampaignDeviceTagMapRepository campaignDeviceTagMapRepository;
    private final SupportModelRepository supportModelRepository;
    private final FirmwareRepository firmwareRepository;
    private final PackageRepository packageRepository;
    private final DeviceTagMapRepository deviceTagMapRepository;
    private final DeviceGroupMapRepository deviceGroupMapRepository;
    private final ResourceOwnerService resourceOwnerService;

    public CreatedNotification processNotification(ResourceOwnerDto requestUser, Notification notification) {
        Notifier notifier = notificationProcessors.get(notification.notificationType());
        return notifier.createCampaign(requestUser, notification);
    }

    @Transactional(readOnly = false)
    public CreatedCampaign createCampaign(ResourceOwnerDto requestUser, CampaignRequestDto.CreateCampaignDto createCampaignDto) {
        final String NEW_DEPLOYMENT_PREFIX = "FOTA-";

        // find "FOTA-{LATEST_SEQ}"
        Campaign campaign = campaignRepository.findFirstByNameStartsWithAndCompanyIdOrderByNameDesc(NEW_DEPLOYMENT_PREFIX, requestUser.getCompanyId());
        List<Firmware> firmwares = firmwareRepository.findByModel(createCampaignDto.devices().model(), createCampaignDto.profile().target().value().artifactName());
        if (firmwares.isEmpty()) throw new BizException(FOTACrudErrorCode.FIRMWARE_NOT_FOUND);
        SupportModel supportModel = supportModelRepository.findByName(createCampaignDto.devices().model());
        if (supportModel == null) throw new BizException(FOTACrudErrorCode.SUPPORT_MODEL_NOT_FOUND);

        savePackageIfDoesNotExists(requestUser, createCampaignDto, firmwares, supportModel, 1);

        // create new Campaign
        String newCampaignName = getCampaignSeq(campaign);
        Campaign newCampaign = Campaign.createCampaign(newCampaignName, supportModel.getPlatform().getId(), ConvertedDateString.setStartEndDateTime(), requestUser);
        Campaign savedCampaign = campaignRepository.save(newCampaign);

        // record lists for campaign details result
        List<String> expiredWarranty = new ArrayList<>(), notFound = new ArrayList<>();
        // save campaign details
        for (Firmware firmware : firmwares) {
            // TODO: need to consider about the case of "FULL" package type. does it need to receive FULL/INCREMENTAL option as additional parameters from the user?
            if (PackageType.INCREMENTAL.equals(firmware.getPackageType()) && createCampaignDto.profile().target().value().artifactName().equals(firmware.getVersion())) {
                Package targetPackage = packageRepository.findByFirmwareAndModelAndTargetVersion(firmware, supportModel, createCampaignDto.profile().target().value().artifactName());
                CampaignPackageMap fotaCampaignPackageMap = CampaignPackageMap.prepareSave(savedCampaign, targetPackage);
                campaignPackageMapRepository.save(fotaCampaignPackageMap);
                for (String sn : createCampaignDto.devices().serial()) {
                    Device device = deviceRepository.findBySerialNumber(sn);
                    if (skipInvalidDevice(sn, device, notFound, expiredWarranty)) continue;
                    saveDeviceIntoCampaign(savedCampaign, targetPackage, device);
                    saveDeviceTags(savedCampaign, device);
                    saveDeviceGroup(savedCampaign, device);
                }
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

    private static String getCampaignSeq(Campaign campaign) {
        if (campaign != null) {
            String lastCampaignNumber = campaign.getName().substring("FOTA-".length());
            return "FOTA-" + (Integer.parseInt(lastCampaignNumber) + 1);
        } else {
            return "FOTA-" + 1;
        }
    }

    private static boolean skipInvalidDevice(String sn, Device device, List<String> notFoundDevices, List<String> expiredWarrantyDevices) {
        if (device == null) {
            notFoundDevices.add(sn);
            return true;
        }
        if (device.getValidWarranty().equals(UseType.N)) {
            expiredWarrantyDevices.add(sn);
            return true;
        }
        return false;
    }

    private void saveDeviceIntoCampaign(Campaign savedCampaign, Package fotaPackage, Device device) {
        CampaignDeviceMap campaignDeviceMap = CampaignDeviceMap.prepareSave(savedCampaign, device, fotaPackage.getFirmware().getUploadServerType());
        campaignDeviceMapRepository.save(campaignDeviceMap);
    }

    private void saveDeviceGroup(Campaign savedCampaign, Device device) {
        List<DeviceGroupMap> deviceGroup = deviceGroupMapRepository.findByDevice(device);
        for (DeviceGroupMap dgm : deviceGroup) {
            CampaignDeviceGroupMap fotaCampaignDeviceGroupMap = CampaignDeviceGroupMap.prepareSave(
                    savedCampaign, dgm.getDeviceGroup());
            campaignDeviceGroupMapRepository.save(fotaCampaignDeviceGroupMap);
        }
    }

    private void saveDeviceTags(Campaign savedCampaign, Device device) {
        List<DeviceTagMap> deviceTagMap = deviceTagMapRepository.findByDevice(device);
        for (DeviceTagMap dtm : deviceTagMap) {
            CampaignDeviceTagMap fotaCampaignDeviceTagMap = CampaignDeviceTagMap.prepareSave(
                    savedCampaign, dtm.getDeviceTag());
            campaignDeviceTagMapRepository.save(fotaCampaignDeviceTagMap);
        }
    }

    public FoundCampaignStatus getCampaignStatus(ResourceOwnerDto resourceOwner, CampaignRequestDto.CampaignStatus campaignStatus) {
        List<CampaignStatusAggregation> campaignStatusByCampaignIdAndBetweenDate = campaignRepository.findCampaignStatusByCompanyIdAndCampaignIdAndBetweenDate(
                resourceOwner.getCompanyId(), campaignStatus.deploymentId(), campaignStatus.fromTime(), campaignStatus.toTime()
        );
        return FoundCampaignStatus.from(campaignStatusByCampaignIdAndBetweenDate);
    }

    public FoundCampaignStatusDetail getCampaignStatusDetail(ResourceOwnerDto resourceOwner, CampaignRequestDto.CampaignStatusDetail campaignStatus) {
        CampaignStatusAggregation campaignStatusAggregation = null;
        Campaign targetCampaign = campaignRepository.findByCampaignName(resourceOwner.getCompanyId(), campaignStatus.deploymentId());
        if (campaignStatus.appendStatus()) {
            campaignStatusAggregation = campaignRepository.findCampaignStatusByCampaign(targetCampaign);
        }
        if (targetCampaign == null) {
            throw new BizException(FOTACrudErrorCode.CAMPAIGN_NOT_FOUND);
        }
        PageRequest pageRequest = PageRequest.of(campaignStatus.offset(), campaignStatus.size());
        Page<CampaignDeviceMap> campaignDevices = campaignDeviceMapRepository.findByCampaignDevices(targetCampaign, pageRequest);
        return FoundCampaignStatusDetail.of(campaignStatus.deploymentId(), campaignStatusAggregation, campaignDevices, pageRequest);
    }

    @Transactional(readOnly = false)
    public CancelledCampaign cancelCampaign(String resourceOwnerName, Long deploymentId) {
        Campaign campaign = campaignRepository.findById(deploymentId)
                .orElseThrow(() -> new BizException(FOTACrudErrorCode.CAMPAIGN_NOT_FOUND));

        ResourceOwnerDto resourceOwner = resourceOwnerService.findByResourceOwnerId(resourceOwnerName);

        boolean doesBelongToCompany = resourceOwner.getCompanyId().equals(campaign.getCompanyId());
        if (!doesBelongToCompany) {
            throw new BizException(FOTACrudErrorCode.ATTEMPTED_CANCEL_CAMPAIGN_WITH_NOT_VALID_USER);
        }

        campaignDeviceMapRepository.deleteByCampaign(campaign);
        campaignPackageMapRepository.deleteByCampaign(campaign);
        campaignDeviceGroupMapRepository.deleteByCampaign(campaign);
        campaignDeviceTagMapRepository.deleteByCampaign(campaign);
        campaignDeviceMapRepository.flush();
        campaignPackageMapRepository.flush();
        campaignDeviceGroupMapRepository.flush();
        campaignDeviceTagMapRepository.flush();
        campaignRepository.deleteById(deploymentId);

        return CancelledCampaign.builder()
                .deploymentId(deploymentId)
                .action("canceled")
                .message("cancel deployment success")
                .status("success")
                .code("200")
                .timestamp(LocalDateTime.now())
                .build();
    }

    public FotaReadyDevice getFOTAReadyDevice(Long companyId, CampaignRequestDto.FOTAReadyDevice campaignDevice) {
        Page<FOTAReadyDeviceContent> fotaReadyDevice = deviceRepository.findFOTAReadyDevice(companyId, campaignDevice);
        return FotaReadyDevice.of(fotaReadyDevice);
    }
}
