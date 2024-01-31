package com.bos.resource.app.fota.service.updatetype;

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
import com.bos.resource.app.fota.model.dto.ConvertedDateString;
import com.bos.resource.app.fota.model.entity.Package;
import com.bos.resource.app.fota.model.entity.*;
import com.bos.resource.app.fota.repository.*;
import com.bos.resource.app.fota.repository.devicemap.CampaignDeviceMapRepository;
import com.bos.resource.app.fota.repository.firmware.FirmwareRepository;
import com.bos.resource.app.resourceowner.exception.ResourceOwnerErrorCode;
import com.bos.resource.app.resourceowner.model.dto.ResourceOwnerDto;
import com.bos.resource.app.resourceowner.model.entity.Company;
import com.bos.resource.app.resourceowner.repository.CompanyRepository;
import com.bos.resource.exception.common.BizException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.bos.resource.app.resourceowner.exception.ResourceOwnerErrorCode.COMPANY_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class UpdateTypeHelper {

    private final CampaignRepository campaignRepository;
    private final SupportModelRepository supportModelRepository;
    private final DeviceRepository deviceRepository;
    private final FirmwareRepository firmwareRepository;
    private final PackageRepository packageRepository;
    private final CampaignPackageMapRepository campaignPackageMapRepository;
    private final CampaignDeviceGroupMapRepository campaignDeviceGroupMapRepository;
    private final CampaignDeviceTagMapRepository campaignDeviceTagMapRepository;
    private final CampaignDeviceMapRepository campaignDeviceMapRepository;
    private final DeviceTagMapRepository deviceTagMapRepository;
    private final DeviceGroupMapRepository deviceGroupMapRepository;
    private final CompanyRepository companyRepository;
    final String DEPLOYMENT_PREFIX = "FOTA-";

    CreateCampaignIngredients prepareToSave(ResourceOwnerDto requestUser, CampaignRequestDto.CreateCampaignDto createCampaignDto) {
        // find "FOTA-{LATEST_SEQ}", if not exists, then create "FOTA-1"
        Campaign campaign = campaignRepository.findFirstByNameStartsWithAndCompanyIdOrderByNameDesc(DEPLOYMENT_PREFIX, requestUser.getCompanyId());
        SupportModel supportModel = supportModelRepository.findByName(createCampaignDto.devices().model());
        if (supportModel == null) throw new BizException(FOTACrudErrorCode.SUPPORT_MODEL_NOT_FOUND);
        Company company = companyRepository.findById(requestUser.getCompanyId())
                .orElseThrow(() -> new BizException(COMPANY_NOT_FOUND));

        Campaign newCampaign = Campaign.createCampaign(
                getNewCampaignName(campaign),
                requestUser,
                company.getName(),
                supportModel.getPlatform().getId(),
                ConvertedDateString.setStartEndDateTime(createCampaignDto.rules().install()),
                createCampaignDto.rules().install().allowUserPostpone() ? UseType.Y : UseType.N
        );

        return new CreateCampaignIngredients(newCampaign, supportModel);
    }

    String getNewCampaignName(Campaign campaign) {
        if (campaign != null) {
            String lastCampaignNumber = campaign.getName().substring(DEPLOYMENT_PREFIX.length());
            return DEPLOYMENT_PREFIX + (Integer.parseInt(lastCampaignNumber) + 1);
        } else {
            return DEPLOYMENT_PREFIX + 1;
        }
    }

    boolean skipInvalidDevice(String sn, Device device) {
        if (device == null) return true;
        if (device.getValidWarranty().equals(UseType.N)) return true;
        return false;
    }

    /*boolean skipInvalidDevice(String sn, Device device, List<String> notFoundDevices, List<String> expiredWarrantyDevices) {
        if (device == null) {
            notFoundDevices.add(sn);
            return true;
        }
        if (device.getValidWarranty().equals(UseType.N)) {
            expiredWarrantyDevices.add(sn);
            return true;
        }
        return false;
    }*/

    void saveDeviceIntoCampaign(Campaign savedCampaign, Package fotaPackage, Device device) {
        CampaignDeviceMap campaignDeviceMap = CampaignDeviceMap.prepareSave(savedCampaign, device, fotaPackage.getFirmware().getUploadServerType());
        campaignDeviceMapRepository.save(campaignDeviceMap);
    }

    void saveDeviceGroup(Campaign savedCampaign, Device device) {
        List<DeviceGroupMap> deviceGroup = deviceGroupMapRepository.findByDevice(device);
        for (DeviceGroupMap dgm : deviceGroup) {
            CampaignDeviceGroupMap fotaCampaignDeviceGroupMap = CampaignDeviceGroupMap.prepareSave(
                    savedCampaign, dgm.getDeviceGroup());
            campaignDeviceGroupMapRepository.save(fotaCampaignDeviceGroupMap);
        }
    }

    void saveDeviceTags(Campaign savedCampaign, Device device) {
        List<DeviceTagMap> deviceTagMap = deviceTagMapRepository.findByDevice(device);
        for (DeviceTagMap dtm : deviceTagMap) {
            CampaignDeviceTagMap fotaCampaignDeviceTagMap = CampaignDeviceTagMap.prepareSave(
                    savedCampaign, dtm.getDeviceTag());
            campaignDeviceTagMapRepository.save(fotaCampaignDeviceTagMap);
        }
    }

    void saveCampaignDetails(Campaign savedCampaign, Package targetPackage, List<String> serialNumbers) {
        CampaignPackageMap fotaCampaignPackageMap = CampaignPackageMap.prepareSave(savedCampaign, targetPackage);
        campaignPackageMapRepository.save(fotaCampaignPackageMap);
        for (String sn : serialNumbers) {
            Device device = deviceRepository.findBySerialNumber(sn);
            if (skipInvalidDevice(sn, device)) continue;
            saveDeviceIntoCampaign(savedCampaign, targetPackage, device);
            saveDeviceTags(savedCampaign, device);
            saveDeviceGroup(savedCampaign, device);
        }
    }

    /*void saveCampaignDetails(Campaign savedCampaign, Package targetPackage, List<String> serialNumbers, List<String> notFound, List<String> expiredWarranty) {
        CampaignPackageMap fotaCampaignPackageMap = CampaignPackageMap.prepareSave(savedCampaign, targetPackage);
        campaignPackageMapRepository.save(fotaCampaignPackageMap);
        for (String sn : serialNumbers) {
            Device device = deviceRepository.findBySerialNumber(sn);
            if (skipInvalidDevice(sn, device, notFound, expiredWarranty)) continue;
            saveDeviceIntoCampaign(savedCampaign, targetPackage, device);
            saveDeviceTags(savedCampaign, device);
            saveDeviceGroup(savedCampaign, device);
        }
    }*/

    record CreateCampaignIngredients(
        Campaign newCampaign,
        SupportModel supportModel
    ) {}
}
