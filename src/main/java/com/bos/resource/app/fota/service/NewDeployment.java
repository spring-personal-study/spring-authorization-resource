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
import com.bos.resource.app.fota.model.dto.CampaignRegistrationResult;
import com.bos.resource.app.fota.model.dto.CampaignRequestDto;
import com.bos.resource.app.fota.model.dto.CampaignResponseDto;
import com.bos.resource.app.fota.model.dto.ConvertedDateString;
import com.bos.resource.app.fota.model.entity.Package;
import com.bos.resource.app.fota.model.entity.*;
import com.bos.resource.app.fota.model.enums.PackageType;
import com.bos.resource.app.fota.repository.*;
import com.bos.resource.app.fota.repository.firmware.FirmwareRepository;
import com.bos.resource.app.resourceowner.model.dto.ResourceOwnerDto;
import com.bos.resource.exception.common.BizException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("NEW_DEPLOYMENT")
@RequiredArgsConstructor
public class NewDeployment implements Notifier {

    private final CampaignRepository campaignRepository;
    private final FirmwareRepository firmwareRepository;
    private final PackageRepository packageRepository;
    private final SupportModelRepository supportModelRepository;
    private final CampaignPackageMapRepository campaignPackageMapRepository;
    private final DeviceRepository deviceRepository;
    private final CampaignDeviceMapRepository campaignDeviceMapRepository;
    private final CampaignDeviceTagMapRepository campaignDeviceTagMapRepository;
    private final CampaignDeviceGroupMapRepository campaignDeviceGroupMapRepository;
    private final DeviceTagMapRepository deviceTagMapRepository;
    private final DeviceGroupMapRepository deviceGroupMapRepository;

    @Transactional
    @Override
    public CampaignResponseDto.CreatedNotification createCampaign(ResourceOwnerDto requestUser, CampaignRequestDto.Notification notification, Pageable pageable) {
        final String NEW_DEPLOYMENT_PREFIX = "FOTA-";

        // find "FOTA-{LATEST_SEQ}"
        Campaign campaign = campaignRepository.findFirstByNameStartsWithAndCompanyIdOrderByNameDesc(NEW_DEPLOYMENT_PREFIX, requestUser.getCompanyId());
        Page<Firmware> firmwares = firmwareRepository.findByModelPaging(notification.params().model(), pageable);
        if (firmwares.getContent().isEmpty()) throw new BizException(FOTACrudErrorCode.FIRMWARE_NOT_FOUND);
        // find targetBuild's firmware
        Firmware firmware = firmwares.getContent().stream()
                .filter(e -> PackageType.INCREMENTAL.equals(e.getPackageType()))
                .filter(e -> notification.params().targetBuild().equals(e.getVersion()))
                .findFirst()
                .orElseGet(() -> firmwares.getContent().get(0));
        SupportModel supportModel = supportModelRepository.findByName(notification.params().model());
        if (supportModel == null) throw new BizException(FOTACrudErrorCode.SUPPORT_MODEL_NOT_FOUND);

        savePackageIfDoesNotExists(requestUser, notification, firmwares, supportModel, 1);

        // create new Campaign
        String newCampaignName = getCampaignSeq(campaign, NEW_DEPLOYMENT_PREFIX);
        Campaign newCampaign = Campaign.createCampaign(newCampaignName, supportModel.getPlatform().getId(), ConvertedDateString.setStartEndDateTime(), requestUser);
        Campaign savedCampaign = campaignRepository.save(newCampaign);
        // save campaign details
        Package targetPackage = packageRepository.findByFirmwareAndModelAndTargetVersion(firmware, supportModel, notification.params().targetBuild());
        CampaignRegistrationResult result = saveDeviceInfos(notification.params().serialNumbers(), savedCampaign, targetPackage);
        return CampaignResponseDto.CreatedNotification.of(firmwares, result);
    }

    private void savePackageIfDoesNotExists(ResourceOwnerDto requestUser, CampaignRequestDto.Notification notification, Page<Firmware> firmwares, SupportModel supportModel, int seq) {
        for (Firmware firmware : firmwares.getContent()) {
            Package targetPackage = packageRepository.findByFirmwareAndModelAndTargetVersion(firmware, supportModel, notification.params().targetBuild());
            String packageNamePrefix = firmware.getUploadServerType().getType() + "-PACKAGE-";
            if (targetPackage != null && targetPackage.getPackageName().startsWith(packageNamePrefix)) {
                seq = Integer.parseInt(targetPackage.getPackageName().substring(packageNamePrefix.length())) + 1;
            }
            if (targetPackage == null) {
                Package newPackage = Package.createPackage((packageNamePrefix + seq++), firmware, supportModel, requestUser);
                packageRepository.save(newPackage);
            }
        }
    }

    private static String getCampaignSeq(Campaign campaign, String NEW_DEPLOYMENT_PREFIX) {
        if (campaign != null) {
            String lastCampaignNumber = campaign.getName().substring(NEW_DEPLOYMENT_PREFIX.length());
            return NEW_DEPLOYMENT_PREFIX + Integer.parseInt(lastCampaignNumber) + 1;
        } else {
            return NEW_DEPLOYMENT_PREFIX + 1;
        }
    }

    private CampaignRegistrationResult saveDeviceInfos(List<String> serialNumbers, Campaign savedCampaign, Package fotaPackage) {
        CampaignPackageMap fotaCampaignPackageMap = CampaignPackageMap.prepareSave(savedCampaign, fotaPackage);
        campaignPackageMapRepository.save(fotaCampaignPackageMap);
        List<String> successToAddDevicesIntoCampaign = new ArrayList<>();
        List<String> expiredWarranty = new ArrayList<>(), notFound = new ArrayList<>();
        for (String sn : serialNumbers) {
            Device device = deviceRepository.findBySerialNumber(sn);
            if (validateDevice(sn, device, notFound, expiredWarranty)) continue;
            saveDeviceIntoCampaign(savedCampaign, fotaPackage, device);
            saveDeviceTags(savedCampaign, device);
            saveDeviceGroup(savedCampaign, device);
            successToAddDevicesIntoCampaign.add(sn);
        }
        return new CampaignRegistrationResult(successToAddDevicesIntoCampaign, expiredWarranty, notFound);
    }

    private static boolean validateDevice(String sn, Device device, List<String> notFoundDevices, List<String> expiredWarrantyDevices) {
        if (device == null) {
            notFoundDevices.add(sn);
            return true;
        }
        if (device.getValidWarranty().equals(UseType.N)) expiredWarrantyDevices.add(sn);
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


}
