package com.bos.resource.app.fota.service;

import com.bos.resource.app.fota.model.dto.CampaignRequestDto;
import com.bos.resource.app.fota.model.dto.CampaignResponseDto;
import com.bos.resource.app.resourceowner.model.dto.ResourceOwnerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("NEW_DEPLOYMENT")
@RequiredArgsConstructor
public class NewDeployment implements Notifier {

  /*  private final CampaignRepository campaignRepository;
    private final FirmwareRepository firmwareRepository;
    private final PackageRepository packageRepository;
    private final SupportModelRepository supportModelRepository;
    private final CampaignPackageMapRepository campaignPackageMapRepository;
    private final DeviceRepository deviceRepository;
    private final CampaignDeviceMapRepository campaignDeviceMapRepository;
    private final CampaignDeviceTagMapRepository campaignDeviceTagMapRepository;
    private final CampaignDeviceGroupMapRepository campaignDeviceGroupMapRepository;
    private final DeviceTagMapRepository deviceTagMapRepository;
    private final DeviceGroupMapRepository deviceGroupMapRepository;*/

    @Transactional
    @Override
    public CampaignResponseDto.CreatedNotification createCampaign(ResourceOwnerDto requestUser, CampaignRequestDto.Notification notification, Pageable pageable) {
        return null;
       /* final String NEW_DEPLOYMENT_PREFIX = "FOTA-";

        // find "FOTA-{LATEST_SEQ}"
        Campaign campaign = campaignRepository.findFirstByNameStartsWithAndCompanyIdOrderByNameDesc(NEW_DEPLOYMENT_PREFIX, requestUser.getCompanyId());
        Page<Firmware> firmwares = firmwareRepository.findByModelPaging(notification.params().model(), notification.params().targetBuild(), pageable);
        if (firmwares.getContent().isEmpty()) throw new BizException(FOTACrudErrorCode.FIRMWARE_NOT_FOUND);
        SupportModel supportModel = supportModelRepository.findByName(notification.params().model());
        if (supportModel == null) throw new BizException(FOTACrudErrorCode.SUPPORT_MODEL_NOT_FOUND);

        savePackageIfDoesNotExists(requestUser, notification, firmwares, supportModel, 1);

        // create new Campaign
        String newCampaignName = getCampaignSeq(campaign);
        Campaign newCampaign = Campaign.createCampaign(newCampaignName, supportModel.getPlatform().getId(), ConvertedDateString.setStartEndDateTime(), requestUser);
        Campaign savedCampaign = campaignRepository.save(newCampaign);

        // record lists for campaign details result
        List<String> successToAddDevicesIntoCampaign = new ArrayList<>();
        List<String> expiredWarranty = new ArrayList<>(), notFound = new ArrayList<>();
        Map<DeviceWithCampaignFailureType, List<String>> failToAddDeviceIntoCampaign = new HashMap<>();
        // save campaign details
        for (Firmware firmware : firmwares.getContent()) {
            // TODO: need to consider about the case of "FULL" package type. does it need to receive FULL/INCREMENTAL option as additional parameters from the user?
            if (PackageType.INCREMENTAL.equals(firmware.getPackageType()) && notification.params().targetBuild().equals(firmware.getVersion())) {
                Package targetPackage = packageRepository.findByFirmwareAndModelAndTargetVersion(firmware, supportModel, notification.params().targetBuild());
                CampaignPackageMap fotaCampaignPackageMap = CampaignPackageMap.prepareSave(savedCampaign, targetPackage);
                campaignPackageMapRepository.save(fotaCampaignPackageMap);
                for (String sn : notification.params().serialNumbers()) {
                    Device device = deviceRepository.findBySerialNumber(sn);
                    if (skipInvalidDevice(sn, device, notFound, expiredWarranty)) continue;
                    saveDeviceIntoCampaign(savedCampaign, targetPackage, device);
                    saveDeviceTags(savedCampaign, device);
                    saveDeviceGroup(savedCampaign, device);
                    successToAddDevicesIntoCampaign.add(sn);
                }
                failToAddDeviceIntoCampaign.put(EXPIRED_WARRANTY, expiredWarranty);
                failToAddDeviceIntoCampaign.put(NOT_FOUND, notFound);
            }
        }
        CampaignRegistrationResult result = new CampaignRegistrationResult(successToAddDevicesIntoCampaign, failToAddDeviceIntoCampaign);
        return CampaignResponseDto.CreatedNotification.of(NotificationType.NEW_DEPLOYMENT, firmwares, savedCampaign, result, pageable);*/
    }

   /* private void savePackageIfDoesNotExists(ResourceOwnerDto requestUser, CampaignRequestDto.Notification notification, Page<Firmware> firmwares, SupportModel supportModel, int seq) {
        for (Firmware firmware : firmwares.getContent()) {
            Package targetPackage = packageRepository.findByFirmwareAndModelAndTargetVersion(firmware, supportModel, notification.params().targetBuild());
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
*/

}
