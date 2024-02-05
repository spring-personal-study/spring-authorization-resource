package com.bos.resource.unit.entity.fota;

import com.bos.resource.app.common.domain.enums.UseType;
import com.bos.resource.app.device.model.entity.Device;
import com.bos.resource.app.device.model.entity.DeviceGroup;
import com.bos.resource.app.device.model.entity.DeviceTag;
import com.bos.resource.app.device.model.entity.SupportModel;
import com.bos.resource.app.device.model.enums.DeviceStatus;
import com.bos.resource.app.fota.model.constants.enums.OpCode;
import com.bos.resource.app.fota.model.entity.Package;
import com.bos.resource.app.fota.model.entity.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.bos.resource.app.fota.model.constants.enums.CampaignStatus.ACTIVE;
import static org.assertj.core.api.Assertions.assertThat;

public class FOTAEntityTest {

    private final Campaign campaign = new Campaign(
            1L,
            "new campaign name",
            1,
            UseType.N,
            "20281231",
            "00:00",
            "20290101",
            "00:00",
            UseType.Y,
            UseType.Y,
            1L,
            "resourceOwnerId",
            "email@domain.com",
            "companyName"
    );

    private final Device device = new Device(
            1L,
            "EF550ANKLF1905",
            "asdfjkds3429dcds",
            "EF550_ANKLF1905",
            "EF550_ANKLF1905_description",
            DeviceStatus.CREATED,
            LocalDateTime.now(),
            UseType.Y,
            null,
            null);

    @Nested
    @DisplayName("Campaign Entity")
    class CampaignEntityTest {

        @Test
        @DisplayName("Create Campaign Entity - OK")
        void createCampaignEntity_Ok() {
            assertThat(campaign).isNotNull();
            assertThat(campaign.getId()).isEqualTo(1L);
            assertThat(campaign.getName()).isEqualTo("new campaign name");
            assertThat(campaign.getPlatformId()).isEqualTo(1);
            assertThat(campaign.getP2pModeYn()).isEqualTo(UseType.N);
            assertThat(campaign.getStartDate()).isEqualTo("20281231");
            assertThat(campaign.getStartTime()).isEqualTo("00:00");
            assertThat(campaign.getEndDate()).isEqualTo("20290101");
            assertThat(campaign.getEndTime()).isEqualTo("00:00");
            assertThat(campaign.getAskUserYn()).isEqualTo(UseType.Y);
            assertThat(campaign.getLteUseYn()).isEqualTo(UseType.Y);
            assertThat(campaign.getCompanyId()).isEqualTo(1L);
            assertThat(campaign.getCreateId()).isEqualTo("email@domain.com");
            assertThat(campaign.getUpdateId()).isEqualTo("email@domain.com");
            assertThat(campaign.getStatus()).isEqualTo(ACTIVE);
            assertThat(campaign.getUseYn()).isEqualTo(UseType.Y);

        }
    }

    @Nested
    @DisplayName("CampaignDeviceMap Entity")
    class CampaignDeviceMapEntityTest {

        @Test
        @DisplayName("Create CampaignDeviceMap Entity - OK")
        void createCampaignDeviceMapEntity_Ok() {

            Campaign campaign = new Campaign(
                    1L,
                    "new campaign name",
                    1,
                    UseType.N,
                    "20281231",
                    "00:00",
                    "20290101",
                    "00:00",
                    UseType.Y,
                    UseType.Y,
                    1L,
                    "resourceOwnerId",
                    "email@domain.com",
                    "companyName"
            );

            Device device = new Device(
                    1L,
                    "EF550ANKLF1905",
                    "asdfjkds3429dcds",
                    "EF550_ANKLF1905",
                    "EF550_ANKLF1905_description",
                    DeviceStatus.CREATED,
                    LocalDateTime.now(),
                    UseType.Y,
                    null,
                    null);

            CampaignDeviceMap campaignDeviceMap = CampaignDeviceMap.builder()
                    .id(1L)
                    .campaign(campaign)
                    .device(device)
                    .build();

            assertThat(campaignDeviceMap).isNotNull();
            assertThat(campaignDeviceMap.getId()).isEqualTo(1L);
            assertThat(campaignDeviceMap.getCampaign()).isEqualTo(campaign);
            assertThat(campaignDeviceMap.getDevice()).isEqualTo(device);
        }
    }

    @Nested
    @DisplayName("CampaignDeviceTagMap Entity")
    class CampaignDeviceTagMapEntityTest {

        @Test
        @DisplayName("Create CampaignDeviceTagMap Entity - OK")
        void createCampaignDeviceTagMapEntity_Ok() {

            DeviceTag deviceTag = new DeviceTag(
                    1L,
                    "name",
                    "description",
                    null,
                    UseType.Y,
                    "createId",
                    "updateId"
            );

            CampaignDeviceTagMap campaignDeviceTagMap = CampaignDeviceTagMap.builder()
                    .id(1L)
                    .campaign(campaign)
                    .deviceTag(deviceTag)
                    .build();

            assertThat(campaignDeviceTagMap).isNotNull();
            assertThat(campaignDeviceTagMap.getId()).isEqualTo(1L);
            assertThat(campaignDeviceTagMap.getCampaign()).isEqualTo(campaign);
            assertThat(campaignDeviceTagMap.getDeviceTag()).isEqualTo(deviceTag);
        }
    }

    @Nested
    @DisplayName("CampaignDeviceGroupMap Entity")
    class CampaignDeviceGroupMapEntityTest {

        @Test
        @DisplayName("Create CampaignDeviceGroupMap Entity - OK")
        void createCampaignDeviceGroupMapEntity_Ok() {

            DeviceGroup deviceGroup = new DeviceGroup(
                    1L,
                    "name",
                    "description",
                    null,
                    null,
                    null,
                    UseType.Y
            );

            CampaignDeviceGroupMap campaignDeviceGroupMap = CampaignDeviceGroupMap.builder()
                    .id(1L)
                    .campaign(campaign)
                    .deviceGroup(deviceGroup)
                    .build();

            assertThat(campaignDeviceGroupMap).isNotNull();
            assertThat(campaignDeviceGroupMap.getId()).isEqualTo(1L);
            assertThat(campaignDeviceGroupMap.getCampaign()).isEqualTo(campaign);
            assertThat(campaignDeviceGroupMap.getDeviceGroup()).isEqualTo(deviceGroup);
        }
    }

    @Nested
    @DisplayName("CampaignPackageMap Entity")
    class CampaignPackageMapEntityTest {

        @Test
        @DisplayName("Create CampaignPackageMap Entity - OK")
        void createCampaignPackageMapEntity_Ok() {

            Package fotaPackage = new Package(
                    1L,
                    "packageName",
                    "description",
                    "currentVersion",
                    "targetVersion",
                    UseType.Y,
                    null,
                    null,
                    1L,
                    1L,
                    "email"
            );

            CampaignPackageMap campaignPackageMap = CampaignPackageMap.builder()
                    .id(1L)
                    .campaign(campaign)
                    .fotaPackage(fotaPackage)
                    .build();

            assertThat(campaignPackageMap).isNotNull();
            assertThat(campaignPackageMap.getId()).isEqualTo(1L);
            assertThat(campaignPackageMap.getCampaign()).isEqualTo(campaign);
            assertThat(campaignPackageMap.getFotaPackage()).isEqualTo(fotaPackage);
        }
    }

    @Nested
    @DisplayName("Firmware Entity")
    class FirmwareEntityTest {

        @Test
        @DisplayName("Create Firmware Entity - OK")
        void createFirmwareEntity_Ok() {

            Firmware firmware = new Firmware(
                    1L,
                    "name",
                    "version",
                    "model",
                    1L,
                    "uuid",
                    "originFileName",
                    "url",
                    "description",
                    null,
                    null,
                    UseType.Y,
                    1L,
                    1L
            );

            assertThat(firmware).isNotNull();
            assertThat(firmware.getId()).isEqualTo(1L);
            assertThat(firmware.getName()).isEqualTo("name");
            assertThat(firmware.getVersion()).isEqualTo("version");
            assertThat(firmware.getModel()).isEqualTo("model");
            assertThat(firmware.getOsVersion()).isEqualTo(1L);
            assertThat(firmware.getUuid()).isEqualTo("uuid");
            assertThat(firmware.getOriginFileName()).isEqualTo("originFileName");
            assertThat(firmware.getUrl()).isEqualTo("url");
            assertThat(firmware.getDescription()).isEqualTo("description");
            assertThat(firmware.getUploadServerType()).isNull();
            assertThat(firmware.getPackageType()).isNull();
            assertThat(firmware.getUseYn()).isEqualTo(UseType.Y);
            assertThat(firmware.getPlatformId()).isEqualTo(1L);
            assertThat(firmware.getCompanyId()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("OperationQueue Entity")
    class OperationQueueEntityTest {

        @Test
        @DisplayName("Create OperationQueue Entity - OK")
        void createOperationQueueEntity_Ok() {

            OperationQueue operationQueue = new OperationQueue(
                1L,
                device,
                OpCode.UPGRADE_FIRMWARE,
                "payLoad",
                LocalDateTime.now()
            );

            assertThat(operationQueue).isNotNull();
            assertThat(operationQueue.getId()).isEqualTo(1L);
            assertThat(operationQueue.getOpCode()).isEqualTo(OpCode.UPGRADE_FIRMWARE);
            assertThat(operationQueue.getDevice()).isEqualTo(device);
            assertThat(operationQueue.getPayLoad()).isEqualTo("payLoad");
            assertThat(operationQueue.getUpdateDateTime()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Package Entity")
    class PackageEntityTest {

        @Test
        @DisplayName("Create Package Entity - OK")
        void createPackageEntity_Ok() {

            Firmware firmware = Firmware.builder()
                    .id(1L)
                    .version("version")
                    .packageType(null)
                    .uploadServerType(null)
                    .build();

            SupportModel supportModel = SupportModel.builder()
                    .id(1L)
                    .platform(null)
                    .name("name")
                    .useType(UseType.Y)
                    .build();

            Package fotaPackage = new Package(
                    1L,
                    "packageName",
                    "description",
                    "currentVersion",
                    "targetVersion",
                    UseType.Y,
                    supportModel,
                    firmware,
                    1L,
                    1L,
                    "email"
            );

            assertThat(fotaPackage).isNotNull();
            assertThat(fotaPackage.getPackageId()).isEqualTo(1L);
            assertThat(fotaPackage.getPackageName()).isEqualTo("packageName");
            assertThat(fotaPackage.getDescription()).isEqualTo("description");
            assertThat(fotaPackage.getCurrentVersion()).isEqualTo("currentVersion");
            assertThat(fotaPackage.getTargetVersion()).isEqualTo("targetVersion");
            assertThat(fotaPackage.getUseYn()).isEqualTo(UseType.Y);
            assertThat(fotaPackage.getModel()).isEqualTo(supportModel);
            assertThat(fotaPackage.getFirmware()).isEqualTo(firmware);
            assertThat(fotaPackage.getCompanyId()).isEqualTo(1L);
            assertThat(fotaPackage.getPlatformId()).isEqualTo(1L);
            assertThat(fotaPackage.getCreateId()).isEqualTo("email");
            assertThat(fotaPackage.getUpdateId()).isEqualTo("email");
        }
    }

}
