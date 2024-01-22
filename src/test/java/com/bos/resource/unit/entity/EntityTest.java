package com.bos.resource.unit.entity;

import com.bos.resource.app.common.domain.enums.UseType;
import com.bos.resource.app.device.model.entity.*;
import com.bos.resource.app.device.model.enums.DeviceStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Entity Test")
public class EntityTest {

    @Nested
    @DisplayName("Device Entity")
    class DeviceEntityTest {

        @Test
        @DisplayName("Create Device Entity - OK")
        void createDeviceEntity_Ok() {

            Device device = Device.builder()
                    .id(1L)
                    .serialNumber("1234567890")
                    .deviceCode("1234567890")
                    .deviceName("deviceName")
                    .deviceDescription("deviceDescription")
                    .status(DeviceStatus.CREATED)
                    .enrolmentDate(LocalDateTime.now())
                    .validWarranty(UseType.Y)
                    .deviceDetail(null)
                    .resourceOwner(null)
                    .build();

            assertThat(device).isNotNull();
            assertThat(device.getId()).isEqualTo(1L);
            assertThat(device.getSerialNumber()).isEqualTo("1234567890");
            assertThat(device.getDeviceCode()).isEqualTo("1234567890");
            assertThat(device.getDeviceName()).isEqualTo("deviceName");
            assertThat(device.getDeviceDescription()).isEqualTo("deviceDescription");
            assertThat(device.getStatus()).isEqualTo(DeviceStatus.CREATED);
            assertThat(device.getEnrolmentDate()).isNotNull();
            assertThat(device.getValidWarranty()).isEqualTo(UseType.Y);
            assertThat(device.getDeviceDetail()).isNull();
            assertThat(device.getResourceOwner()).isNull();
        }
    }

    @Nested
    @DisplayName("Device Detail Entity")
    class DeviceDetailEntityTest {

        @Test
        @DisplayName("Create Device Detail Entity - OK")
        void createDeviceDetailEntity_Ok() {

            DeviceDetail deviceDetail = DeviceDetail.builder()
                    .id(1L)
                    .serialNumber("1234567890")
                    .imei("1234567890")
                    .imsi("1234567890")
                    .macAddress("1234567890")
                    .modelName("modelName")
                    .vendor("vendor")
                    .osVersion("osVersion")
                    .osBuildDate("osBuildDate")
                    .build();

            assertThat(deviceDetail).isNotNull();
            assertThat(deviceDetail.getId()).isEqualTo(1L);
            assertThat(deviceDetail.getSerialNumber()).isEqualTo("1234567890");
            assertThat(deviceDetail.getImei()).isEqualTo("1234567890");
            assertThat(deviceDetail.getImsi()).isEqualTo("1234567890");
            assertThat(deviceDetail.getMacAddress()).isEqualTo("1234567890");
            assertThat(deviceDetail.getModelName()).isEqualTo("modelName");
            assertThat(deviceDetail.getVendor()).isEqualTo("vendor");
            assertThat(deviceDetail.getOsVersion()).isEqualTo("osVersion");
            assertThat(deviceDetail.getOsBuildDate()).isEqualTo("osBuildDate");

        }

    }

    @Nested
    @DisplayName("Device Group Entity")
    class DeviceGroupEntityTest {

        @Test
        @DisplayName("Create Device Group Entity - OK")
        void createDeviceGroupEntity_Ok() {

            DeviceGroup deviceGroup = DeviceGroup.builder()
                    .id(1L)
                    .groupCode("groupCode")
                    .groupName("groupName")
                    .groupDescription("groupDescription")
                    .useType(UseType.Y)
                    .build();

            assertThat(deviceGroup).isNotNull();
            assertThat(deviceGroup.getId()).isEqualTo(1L);
            assertThat(deviceGroup.getGroupCode()).isEqualTo("groupCode");
            assertThat(deviceGroup.getGroupName()).isEqualTo("groupName");
            assertThat(deviceGroup.getGroupDescription()).isEqualTo("groupDescription");
            assertThat(deviceGroup.getUseType()).isEqualTo(UseType.Y);
        }
    }

    @Nested
    @DisplayName("Device GroupMap Entity")
    class DeviceGroupMapEntityTest {

        @Test
        @DisplayName("Create Device Group Map Entity - OK")
        void createDeviceGroupMapEntity_Ok() {

            Device device = Device.builder()
                    .id(1L)
                    .serialNumber("1234567890")
                    .deviceCode("1234567890")
                    .deviceName("deviceName")
                    .deviceDescription("deviceDescription")
                    .status(DeviceStatus.CREATED)
                    .enrolmentDate(LocalDateTime.now())
                    .validWarranty(UseType.Y)
                    .deviceDetail(null)
                    .resourceOwner(null)
                    .build();

            DeviceGroup deviceGroup = DeviceGroup.builder()
                    .id(1L)
                    .groupCode("groupCode")
                    .groupName("groupName")
                    .groupDescription("groupDescription")
                    .useType(UseType.Y)
                    .build();

            DeviceGroupMap deviceGroupMap = DeviceGroupMap.builder()
                    .id(1L)
                    .device(device)
                    .deviceGroup(deviceGroup)
                    .build();

            assertThat(deviceGroupMap).isNotNull();
            assertThat(deviceGroupMap.getId()).isEqualTo(1L);
            assertThat(deviceGroupMap.getDevice()).isEqualTo(device);
            assertThat(deviceGroupMap.getDeviceGroup()).isEqualTo(deviceGroup);
        }
    }

    @Nested
    @DisplayName("Device Platform Entity")
    class DevicePlatformEntityTest {

        @Test
        @DisplayName("Create Device Platform Entity - OK")
        void createDevicePlatformEntity_Ok() {

            DevicePlatform devicePlatform = DevicePlatform.builder()
                    .id(1)
                    .platformName("platformName")
                    .useYn(UseType.Y)
                    .createId("createId")
                    .updateId("updateId")
                    .build();

            assertThat(devicePlatform).isNotNull();
            assertThat(devicePlatform.getId()).isEqualTo(1);
            assertThat(devicePlatform.getPlatformName()).isEqualTo("platformName");
            assertThat(devicePlatform.getUseYn()).isEqualTo(UseType.Y);
            assertThat(devicePlatform.getCreateId()).isEqualTo("createId");
            assertThat(devicePlatform.getUpdateId()).isEqualTo("updateId");
        }

    }

    @Nested
    @DisplayName("Device Tag Entity")
    class DeviceTagEntityTest {

        @Test
        @DisplayName("Create Device Tag Entity - OK")
        void createDeviceTagEntity_Ok() {

                DeviceTag deviceTag = DeviceTag.builder()
                        .id(1L)
                        .name("name")
                        .description("description")
                        .company(null)
                        .useYn(UseType.Y)
                        .createId("createId")
                        .updateId("updateId")
                        .build();

                assertThat(deviceTag).isNotNull();
                assertThat(deviceTag.getId()).isEqualTo(1L);
                assertThat(deviceTag.getName()).isEqualTo("name");
                assertThat(deviceTag.getDescription()).isEqualTo("description");
                assertThat(deviceTag.getCompany()).isNull();
                assertThat(deviceTag.getUseYn()).isEqualTo(UseType.Y);
                assertThat(deviceTag.getCreateId()).isEqualTo("createId");
                assertThat(deviceTag.getUpdateId()).isEqualTo("updateId");

        }

    }

    @Nested
    @DisplayName("Device TagMap Entity")
    class DeviceTagMapEntityTest {

        @Test
        @DisplayName("Create Device TagMap Entity - OK")
        void createDeviceTagMapEntity_Ok() {

            Device device = Device.builder()
                    .id(1L)
                    .serialNumber("1234567890")
                    .deviceCode("1234567890")
                    .deviceName("deviceName")
                    .deviceDescription("deviceDescription")
                    .status(DeviceStatus.CREATED)
                    .enrolmentDate(LocalDateTime.now())
                    .validWarranty(UseType.Y)
                    .deviceDetail(null)
                    .resourceOwner(null)
                    .build();

            DeviceTag deviceTag = DeviceTag.builder()
                    .id(1L)
                    .name("name")
                    .description("description")
                    .company(null)
                    .useYn(UseType.Y)
                    .createId("createId")
                    .updateId("updateId")
                    .build();

            DeviceTagMap deviceTagMap = DeviceTagMap.builder()
                    .id(1L)
                    .device(device)
                    .deviceTag(deviceTag)
                    .build();

            assertThat(deviceTagMap).isNotNull();
            assertThat(deviceTagMap.getId()).isEqualTo(1L);
            assertThat(deviceTagMap.getDevice()).isEqualTo(device);
            assertThat(deviceTagMap.getDeviceTag()).isEqualTo(deviceTag);

        }

    }

    @Nested
    @DisplayName("Device Support Model Entity")
    class DeviceSupportModelEntityTest {

        @Test
        @DisplayName("Create Device SupportModel Entity - OK")
        void createDeviceSupportModelEntity_Ok() {

            DevicePlatform devicePlatform = DevicePlatform.builder()
                    .id(1)
                    .platformName("platformName")
                    .useYn(UseType.Y)
                    .createId("createId")
                    .updateId("updateId")
                    .build();

            SupportModel supportModel = SupportModel.builder()
                    .id(1L)
                    .platform(devicePlatform)
                    .name("modelDescription")
                    .useType(UseType.Y)
                    .build();

            assertThat(supportModel).isNotNull();
            assertThat(supportModel.getId()).isEqualTo(1L);
            assertThat(supportModel.getPlatform()).isEqualTo(devicePlatform);
            assertThat(supportModel.getName()).isEqualTo("modelDescription");
            assertThat(supportModel.getUseType()).isEqualTo(UseType.Y);
        }
    }
}
