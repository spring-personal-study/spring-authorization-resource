package com.bos.resource.slice.service.fota;

import com.bos.resource.app.common.domain.dto.Paging;
import com.bos.resource.app.device.model.entity.Device;
import com.bos.resource.app.device.model.entity.DeviceDetail;
import com.bos.resource.app.device.model.enums.DeviceStatus;
import com.bos.resource.app.device.repository.device.DeviceRepository;
import com.bos.resource.app.fota.exception.FOTACrudErrorCode;
import com.bos.resource.app.fota.model.constants.enums.CampaignStatus;
import com.bos.resource.app.fota.model.constants.enums.NotificationType;
import com.bos.resource.app.fota.model.constants.enums.OpCode;
import com.bos.resource.app.fota.model.constants.enums.PackageType;
import com.bos.resource.app.fota.model.constants.enums.message.FirmwareUpdateType;
import com.bos.resource.app.fota.model.dto.*;
import com.bos.resource.app.fota.model.dto.CampaignRequestDto.CreateCampaignDto;
import com.bos.resource.app.fota.model.dto.CampaignRequestDto.CreateCampaignDto.CampaignDevice;
import com.bos.resource.app.fota.model.dto.CampaignRequestDto.CreateCampaignDto.CampaignProfile;
import com.bos.resource.app.fota.model.dto.CampaignRequestDto.CreateCampaignDto.CampaignRule;
import com.bos.resource.app.fota.model.dto.CampaignRequestDto.CreateCampaignDto.CampaignRule.InstallRule;
import com.bos.resource.app.fota.model.dto.CampaignRequestDto.Notification.PeriodParams;
import com.bos.resource.app.fota.model.dto.CampaignResponseDto.*;
import com.bos.resource.app.fota.model.dto.CampaignResponseDto.FotaReadyDevice.FOTAReadyDeviceContent;
import com.bos.resource.app.fota.model.dto.Notifications.NotificationDetail.NotificationDetailMetadata;
import com.bos.resource.app.fota.model.entity.Campaign;
import com.bos.resource.app.fota.model.entity.CampaignDeviceMap;
import com.bos.resource.app.fota.model.entity.CampaignPackageMap;
import com.bos.resource.app.fota.model.entity.Firmware;
import com.bos.resource.app.fota.repository.*;
import com.bos.resource.app.fota.repository.devicemap.CampaignDeviceMapRepository;
import com.bos.resource.app.fota.service.FOTAService;
import com.bos.resource.app.fota.service.NewArtifactAutoUpdate;
import com.bos.resource.app.fota.service.NewDeployment;
import com.bos.resource.app.fota.service.UpdateNotifier;
import com.bos.resource.app.fota.service.updatetype.UpdateTypeCustom;
import com.bos.resource.app.fota.service.updatetype.UpdateTypeLatest;
import com.bos.resource.app.fota.service.updatetype.UpdateTypeSelector;
import com.bos.resource.app.resourceowner.ResourceOwnerService;
import com.bos.resource.app.resourceowner.model.dto.ResourceOwnerDto;
import com.bos.resource.exception.common.BizException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.bos.resource.app.fota.model.constants.enums.message.FirmwareUpdateType.CUSTOM;
import static com.bos.resource.app.fota.model.constants.strings.FirmwareUpdateTypeConstants.CUSTOM_VALUE;
import static com.bos.resource.app.fota.model.constants.strings.FirmwareUpdateTypeConstants.LATEST_VALUE;
import static java.time.LocalDateTime.now;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@DisplayName("FOTA Service Test - New Artifact Auto Update")
public class FOTAServiceTest {

    private final CampaignRepository campaignRepository = mock(CampaignRepository.class);
    private final DeviceRepository deviceRepository = mock(DeviceRepository.class);
    private final CampaignDeviceMapRepository campaignDeviceMapRepository = mock(CampaignDeviceMapRepository.class);
    private final CampaignPackageMapRepository campaignPackageMapRepository = mock(CampaignPackageMapRepository.class);
    private final CampaignDeviceGroupMapRepository campaignDeviceGroupMapRepository = mock(CampaignDeviceGroupMapRepository.class);
    private final CampaignDeviceTagMapRepository campaignDeviceTagMapRepository = mock(CampaignDeviceTagMapRepository.class);
    private final OperationQueueRepository operationQueueRepository = mock(OperationQueueRepository.class);
    private final ResourceOwnerService resourceOwnerService = mock(ResourceOwnerService.class);
    private final MockedStatic<OperationJson.PayLoad> mockPayLoad = mockStatic(OperationJson.PayLoad.class);

    @AfterEach
    void cleanUp() {
        mockPayLoad.close();
    }

    private final Map<String, UpdateNotifier> notificationProcessors = Map.of(
            NotificationType.NEW_ARTIFACT_AUTO_UPDATE.getName(),
            mock(NewArtifactAutoUpdate.class),
            NotificationType.NEW_DEPLOYMENT.getName(),
            mock(NewDeployment.class)
    );

    private final Map<String, UpdateTypeSelector> updateTypeSelector = Map.of(
            CUSTOM.getName(),
            mock(UpdateTypeCustom.class),
            FirmwareUpdateType.LATEST.getName(),
            mock(UpdateTypeLatest.class)
    );

    private final FOTAService fotaService = new FOTAService(
            resourceOwnerService,
            notificationProcessors,
            updateTypeSelector,
            campaignRepository,
            deviceRepository,
            campaignDeviceMapRepository,
            campaignPackageMapRepository,
            campaignDeviceGroupMapRepository,
            campaignDeviceTagMapRepository,
            operationQueueRepository
    );

    @Nested
    @DisplayName("processNotification test")
    class ProcessNotificationTest {

        ResourceOwnerDto requestUser = new ResourceOwnerDto(
                1L,
                "saved_resourceOwnerId_1",
                "saved_email_1",
                1L
        );

        CampaignRequestDto.Notification parameters = new CampaignRequestDto.Notification(
                NotificationType.NEW_ARTIFACT_AUTO_UPDATE.getName(),
                new PeriodParams(LocalDateTime.parse("2023-01-01T00:00:00"), LocalDateTime.parse("2025-01-01T00:00:00")),
                0,
                10
        );

        private CampaignResponseDto.CreatedNotification getCreatedNotification() {
            NotificationDetailMetadata notificationDetailMetadata_1 = new NotificationDetailMetadata(
                    LocalDateTime.parse("2024-01-01T00:00:00"),
                    PackageType.FULL
            );

            NotificationDetailMetadata notificationDetailMetadata_2 = new NotificationDetailMetadata(
                    LocalDateTime.parse("2024-01-01T00:00:00"),
                    PackageType.INCREMENTAL
            );

            Notifications.NotificationDetail notificationDetail_1 = new Notifications.NotificationDetail(
                    "device_model_name[EF501]",
                    "20240101_R1.00",
                    true,
                    notificationDetailMetadata_1
            );

            Notifications.NotificationDetail notificationDetail_2 = new Notifications.NotificationDetail(
                    "device_model_name[EF501]",
                    "20241201_R1.00",
                    true,
                    notificationDetailMetadata_2
            );

            Notifications notifications_1 = new Notifications(NotificationType.NEW_ARTIFACT_AUTO_UPDATE.getName(), notificationDetail_1);
            Notifications notifications_2 = new Notifications(NotificationType.NEW_ARTIFACT_AUTO_UPDATE.getName(), notificationDetail_2);

            return new CampaignResponseDto.CreatedNotification(
                    new Paging(0, 0, 10, 2),
                    List.of(notifications_1, notifications_2),
                    null
            );
        }

        @Test
        @DisplayName("NEW_ARTIFACT_AUTO_UPDATE 200 OK")
        void processNotificationTest200() {
            UpdateNotifier updateNotifier = notificationProcessors.get(parameters.notificationType());
            given(updateNotifier.notify(requestUser, parameters)).willReturn(getCreatedNotification());

            fotaService.processNotification(requestUser, parameters);

            verify(updateNotifier, atMostOnce()).notify(requestUser, parameters);
        }
    }

    @Nested
    @DisplayName("createCampaign test")
    class CreateCampaignTest {

        ResourceOwnerDto requestUser = new ResourceOwnerDto(
                1L,
                "saved_resourceOwnerId_1",
                "saved_email_1",
                1L
        );

        CampaignProfile.ProfileTarget.TargetValue targetValue = new CampaignProfile.ProfileTarget.TargetValue("20230101_R1.00");
        CampaignProfile.ProfileTarget profileTarget = new CampaignProfile.ProfileTarget(targetValue);
        CampaignProfile profile = new CampaignProfile(CUSTOM_VALUE, profileTarget);

        InstallRule install = new InstallRule("20230131", "19:25", "21:25", false);
        CampaignRule rule = new CampaignRule(install);

        CampaignDevice devices = new CampaignDevice(
                "EF501",
                List.of("EF501ANCLBA192", "EF501ANCLBA193", "EF501ANCLBA194")
        );

        CampaignRequestDto.CreateCampaignDto createCampaignDto = new CreateCampaignDto(profile, rule, devices);

        @Test
        @DisplayName("CUSTOM: 200 OK")
        void createCampaignTest200_CUSTOM() {
            UpdateTypeSelector updateTypeSelected = updateTypeSelector.get(createCampaignDto.profile().updateType());
            CreatedCampaign createdCampaign = new CreatedCampaign(
                    "FOTA-27",
                    "created",
                    "create deployment success",
                    "200",
                    now()
            );
            Optional<Campaign> savedCampaign = Optional.of(Campaign.builder().name("FOTA-27").build());
            CampaignPackageMap mockCampaignPackageMap = mock(CampaignPackageMap.class, RETURNS_DEEP_STUBS);
            Firmware mockFirmware = mock(Firmware.class);

            given(updateTypeSelected.createCampaign(requestUser, createCampaignDto)).willReturn(createdCampaign);
            given(campaignRepository.findByName(createdCampaign.getDeploymentId())).willReturn(savedCampaign);
            given(campaignPackageMapRepository.findByCampaign(savedCampaign.get())).willReturn(mockCampaignPackageMap);
            given(mockCampaignPackageMap.getFotaPackage().getFirmware()).willReturn(mockFirmware);
            given(campaignDeviceMapRepository.findByCampaign(savedCampaign.get())).willReturn(emptyList());
            mockPayLoad.when(() -> OperationJson.PayLoad.getPayLoad(any(), any())).thenReturn(mock(OperationJson.PayLoad.class));

            CreatedCampaign campaign = fotaService.createCampaign(requestUser, createCampaignDto);

            assertThat(campaign).isNotNull();
            assertThat(campaign.getDeploymentId()).isEqualTo(createdCampaign.getDeploymentId());

            verify(updateTypeSelected, atMostOnce()).createCampaign(requestUser, createCampaignDto);
            verify(campaignRepository, atMostOnce()).findByName(createdCampaign.getDeploymentId());
        }

        @Test
        @DisplayName("CUSTOM: 400 BAD REQUEST - missing profile.target || profile.target.value || profile.target.value.artifactName")
        void createCampaignTest400_MissingValuesForCUSTOM_profileTargetValueArtifactName() {
            CreateCampaignDto createCampaignDto = new CreateCampaignDto(new CampaignProfile(CUSTOM_VALUE, null), rule, devices);
            UpdateTypeSelector updateTypeSelected = updateTypeSelector.get(createCampaignDto.profile().updateType());
            given(updateTypeSelected.createCampaign(requestUser, createCampaignDto))
                    .willThrow(new BizException(FOTACrudErrorCode.ARTIFACT_NAME_IS_NULL));

            assertThatThrownBy(() -> fotaService.createCampaign(requestUser, createCampaignDto))
                    .isInstanceOf(BizException.class)
                    .hasMessage(FOTACrudErrorCode.ARTIFACT_NAME_IS_NULL.getMsg());

            verify(updateTypeSelected, atMostOnce()).createCampaign(requestUser, createCampaignDto);
        }

        @Test
        @DisplayName("CUSTOM: 404 Not Found - Firmwares")
        void createCampaignTest404_notFound_firmware_CUSTOM() {
            CreateCampaignDto createCampaignDto = new CreateCampaignDto(new CampaignProfile(CUSTOM_VALUE, null), rule, devices);
            UpdateTypeSelector updateTypeSelected = updateTypeSelector.get(createCampaignDto.profile().updateType());
            given(updateTypeSelected.createCampaign(requestUser, createCampaignDto))
                    .willThrow(new BizException(FOTACrudErrorCode.FIRMWARE_NOT_FOUND));

            assertThatThrownBy(() -> fotaService.createCampaign(requestUser, createCampaignDto))
                    .isInstanceOf(BizException.class)
                    .hasMessage(FOTACrudErrorCode.FIRMWARE_NOT_FOUND.getMsg());

            verify(updateTypeSelected, atMostOnce()).createCampaign(requestUser, createCampaignDto);
        }

        @Test
        @DisplayName("CUSTOM: 404 Not Found - Campaign")
        void createCampaignTest404_notFound_campaign_CUSTOM() {
            CreateCampaignDto createCampaignDto = new CreateCampaignDto(new CampaignProfile(CUSTOM_VALUE, null), rule, devices);
            CreatedCampaign createdCampaign = new CreatedCampaign(
                    "FOTA-27",
                    "created",
                    "create deployment success",
                    "200",
                    now()
            );
            UpdateTypeSelector updateTypeSelected = updateTypeSelector.get(createCampaignDto.profile().updateType());
            given(updateTypeSelected.createCampaign(requestUser, createCampaignDto)).willReturn(createdCampaign);
            given(campaignRepository.findByName(anyString()))
                    .willThrow(new BizException(FOTACrudErrorCode.CAMPAIGN_NOT_FOUND));

            assertThatThrownBy(() -> fotaService.createCampaign(requestUser, createCampaignDto))
                    .isInstanceOf(BizException.class)
                    .hasMessage(FOTACrudErrorCode.CAMPAIGN_NOT_FOUND.getMsg());

            verify(updateTypeSelected, atMostOnce()).createCampaign(requestUser, createCampaignDto);
            verify(campaignRepository, atMostOnce()).findByName(anyString());
        }

        @Test
        @DisplayName("LATEST: 200 OK")
        void createCampaignTest200_LATEST() {

            CampaignProfile profile = new CampaignProfile(LATEST_VALUE, profileTarget);
            CampaignRequestDto.CreateCampaignDto createCampaignDto = new CreateCampaignDto(profile, rule, devices);
            UpdateTypeSelector updateTypeSelected = updateTypeSelector.get(createCampaignDto.profile().updateType());
            CreatedCampaign createdCampaign = new CreatedCampaign(
                    "FOTA-27",
                    "created",
                    "create deployment success",
                    "200",
                    now()
            );
            Optional<Campaign> savedCampaign = Optional.of(Campaign.builder().name("FOTA-27").build());
            CampaignPackageMap mockCampaignPackageMap = mock(CampaignPackageMap.class, RETURNS_DEEP_STUBS);
            Firmware mockFirmware = mock(Firmware.class);

            given(updateTypeSelected.createCampaign(requestUser, createCampaignDto)).willReturn(createdCampaign);
            given(campaignRepository.findByName(createdCampaign.getDeploymentId())).willReturn(savedCampaign);
            given(campaignPackageMapRepository.findByCampaign(savedCampaign.get())).willReturn(mockCampaignPackageMap);
            given(mockCampaignPackageMap.getFotaPackage().getFirmware()).willReturn(mockFirmware);
            given(campaignDeviceMapRepository.findByCampaign(savedCampaign.get())).willReturn(emptyList());
            mockPayLoad.when(() -> OperationJson.PayLoad.getPayLoad(any(), any())).thenReturn(mock(OperationJson.PayLoad.class));

            CreatedCampaign campaign = fotaService.createCampaign(requestUser, createCampaignDto);

            assertThat(campaign).isNotNull();
            assertThat(campaign.getDeploymentId()).isEqualTo(createdCampaign.getDeploymentId());

            verify(updateTypeSelected, atMostOnce()).createCampaign(requestUser, createCampaignDto);
            verify(campaignRepository, atMostOnce()).findByName(createdCampaign.getDeploymentId());
        }

        @Test
        @DisplayName("LATEST: 200 OK - even if missing parameter: profile.target || profile.target.value || profile.target.value.artifactName")
        void createCampaignTest200_thoughMissingTargetParameter_LATEST() {
            CampaignRequestDto.CreateCampaignDto createCampaignDto = new CreateCampaignDto(new CampaignProfile(LATEST_VALUE, null), rule, devices);
            UpdateTypeSelector updateTypeSelected = updateTypeSelector.get(createCampaignDto.profile().updateType());
            CreatedCampaign createdCampaign = new CreatedCampaign(
                    "FOTA-27",
                    "created",
                    "create deployment success",
                    "200",
                    now()
            );
            Optional<Campaign> savedCampaign = Optional.of(Campaign.builder().name("FOTA-27").build());
            CampaignPackageMap mockCampaignPackageMap = mock(CampaignPackageMap.class, RETURNS_DEEP_STUBS);
            Firmware mockFirmware = mock(Firmware.class);

            given(updateTypeSelected.createCampaign(requestUser, createCampaignDto)).willReturn(createdCampaign);
            given(campaignRepository.findByName(createdCampaign.getDeploymentId())).willReturn(savedCampaign);
            given(campaignPackageMapRepository.findByCampaign(savedCampaign.get())).willReturn(mockCampaignPackageMap);
            given(mockCampaignPackageMap.getFotaPackage().getFirmware()).willReturn(mockFirmware);
            given(campaignDeviceMapRepository.findByCampaign(savedCampaign.get())).willReturn(emptyList());
            mockPayLoad.when(() -> OperationJson.PayLoad.getPayLoad(any(), any())).thenReturn(mock(OperationJson.PayLoad.class));

            CreatedCampaign campaign = fotaService.createCampaign(requestUser, createCampaignDto);

            assertThat(campaign).isNotNull();
            assertThat(campaign.getDeploymentId()).isEqualTo(createdCampaign.getDeploymentId());

            verify(updateTypeSelected, atMostOnce()).createCampaign(requestUser, createCampaignDto);
            verify(campaignRepository, atMostOnce()).findByName(createdCampaign.getDeploymentId());

        }

        @Test
        @DisplayName("LATEST: 404 Not Found - Firmwares")
        void createCampaignTest404_notFound_firmware_LATEST() {
            CreateCampaignDto createCampaignDto = new CreateCampaignDto(new CampaignProfile(CUSTOM_VALUE, null), rule, devices);
            UpdateTypeSelector updateTypeSelected = updateTypeSelector.get(createCampaignDto.profile().updateType());
            given(updateTypeSelected.createCampaign(requestUser, createCampaignDto))
                    .willThrow(new BizException(FOTACrudErrorCode.FIRMWARE_NOT_FOUND));

            assertThatThrownBy(() -> fotaService.createCampaign(requestUser, createCampaignDto))
                    .isInstanceOf(BizException.class)
                    .hasMessage(FOTACrudErrorCode.FIRMWARE_NOT_FOUND.getMsg());

            verify(updateTypeSelected, atMostOnce()).createCampaign(requestUser, createCampaignDto);
        }

        @Test
        @DisplayName("LATEST: 404 Not Found - Campaign")
        void createCampaignTest404_notFound_Campaign_LATEST() {
            CreateCampaignDto createCampaignDto = new CreateCampaignDto(new CampaignProfile(LATEST_VALUE, null), rule, devices);
            CreatedCampaign createdCampaign = new CreatedCampaign(
                    "FOTA-27",
                    "created",
                    "create deployment success",
                    "200",
                    now()
            );
            UpdateTypeSelector updateTypeSelected = updateTypeSelector.get(createCampaignDto.profile().updateType());
            given(updateTypeSelected.createCampaign(requestUser, createCampaignDto)).willReturn(createdCampaign);
            given(campaignRepository.findByName(anyString()))
                    .willThrow(new BizException(FOTACrudErrorCode.CAMPAIGN_NOT_FOUND));

            assertThatThrownBy(() -> fotaService.createCampaign(requestUser, createCampaignDto))
                    .isInstanceOf(BizException.class)
                    .hasMessage(FOTACrudErrorCode.CAMPAIGN_NOT_FOUND.getMsg());

            verify(updateTypeSelected, atMostOnce()).createCampaign(requestUser, createCampaignDto);
            verify(campaignRepository, atMostOnce()).findByName(anyString());
        }
    }

    @Nested
    @DisplayName("getCampaignStatus test")
    class GetCampaignStatusTest {

        ResourceOwnerDto resourceOwner = new ResourceOwnerDto(
                1L,
                "saved_resourceOwnerId_1",
                "saved_email_1",
                1L
        );

        CampaignRequestDto.CampaignStatus campaignStatus = new CampaignRequestDto.CampaignStatus(
                List.of("FOTA-27", "FOTA-31"),
                "ALL",
                LocalDateTime.parse("2023-01-01T00:00:00"),
                LocalDateTime.parse("2025-01-01T00:00:00")
        );

        @Test
        @DisplayName("200 OK")
        void getCampaignStatusTest200() {
            List<CampaignStatusAggregation> expectResults = List.of(
                    new CampaignStatusAggregation(
                            "FOTA-27",
                            CampaignStatus.ACTIVE,
                            15L,
                            1,
                            2,
                            3,
                            4,
                            5,
                            now()
                    ),
                    new CampaignStatusAggregation(
                            "FOTA-31",
                            CampaignStatus.INACTIVE,
                            10L,
                            4,
                            3,
                            1,
                            1,
                            1,
                            now()
                    )
            );

            for (int i = 0; i < campaignStatus.deploymentId().size(); i++) {
                CampaignStatusAggregation aggregation = expectResults.get(i);
                given(campaignRepository.findCampaignStatusByCompanyIdAndCampaignIdAndBetweenDateAndStatus(
                        resourceOwner.getCompanyId(),
                        campaignStatus.deploymentId().get(i),
                        campaignStatus.fromTime(),
                        campaignStatus.toTime(),
                        campaignStatus.status()
                )).willReturn(aggregation);
            }

            FoundCampaignStatus found = fotaService.getCampaignStatus(resourceOwner, campaignStatus);

            assertThat(found).isNotNull();
            assertThat(found.getData().size()).isEqualTo(expectResults.size());

            for (int i = 0; i < campaignStatus.deploymentId().size(); i++) {
                CampaignStatusAggregation aggregation = expectResults.get(i);
                assertThat(found.getData().get(i).getDeploymentId()).isEqualTo(aggregation.getDeploymentId());
                assertThat(found.getData().get(i).getTotalDevices()).isEqualTo(aggregation.getTotalDevices());
                assertThat(found.getData().get(i).getCompleted()).isEqualTo(aggregation.getCompleted());
                assertThat(found.getData().get(i).getDownloading()).isEqualTo(aggregation.getDownloading());
                assertThat(found.getData().get(i).getAwaitingInstall()).isEqualTo(aggregation.getAwaitingInstall());
                assertThat(found.getData().get(i).getScheduled()).isEqualTo(aggregation.getScheduled());
                assertThat(found.getData().get(i).getFailed()).isEqualTo(aggregation.getFailed());
                assertThat(found.getData().get(i).getCompletedOn()).isEqualTo(aggregation.getCompletedOn());
            }

            for (int i = 0; i < campaignStatus.deploymentId().size(); i++) {
                verify(campaignRepository, times(1))
                        .findCampaignStatusByCompanyIdAndCampaignIdAndBetweenDateAndStatus(
                                resourceOwner.getCompanyId(),
                                campaignStatus.deploymentId().get(i),
                                campaignStatus.fromTime(),
                                campaignStatus.toTime(),
                                campaignStatus.status()
                        );
            }
        }
    }

    @Nested
    @DisplayName("getCampaignStatusDetail test")
    class GetCampaignStatusDetailTest {

        ResourceOwnerDto resourceOwner = new ResourceOwnerDto(
                1L,
                "saved_resourceOwnerId_1",
                "saved_email_1",
                1L
        );

        CampaignRequestDto.CampaignStatusDetail campaignStatusDetail = new CampaignRequestDto.CampaignStatusDetail(
                "FOTA-27",
                true,
                0,
                10
        );

        @Test
        @DisplayName("200 OK - appendStatus: true")
        void getCampaignStatusDetailTest200() {
            Campaign targetCampaign = Campaign.builder().id(1L).name("FOTA-27").build();
            CampaignStatusAggregation campaignStatusAggregation = new CampaignStatusAggregation(
                    "FOTA-27",
                    CampaignStatus.ACTIVE,
                    15L,
                    1,
                    2,
                    3,
                    4,
                    5,
                    now()
            );
            CampaignDeviceMap campaignDeviceMap1 = CampaignDeviceMap.builder()
                    .campaign(targetCampaign)
                    .device(Device.builder()
                            .deviceName("EF501_EF501ANLKBA159")
                            .serialNumber("EF501ANLKBA159")
                            .status(DeviceStatus.ACTIVE)
                            .deviceDetail(DeviceDetail.builder().modelName("EF501").build())
                            .build())
                    .build();
            CampaignDeviceMap campaignDeviceMap2 = CampaignDeviceMap.builder()
                    .campaign(targetCampaign)
                    .device(Device.builder()
                            .deviceName("EF501_EF501ANLKBA260")
                            .serialNumber("EF501ANLKBA260")
                            .status(DeviceStatus.ACTIVE)
                            .deviceDetail(DeviceDetail.builder().modelName("EF501").build())
                            .build())
                    .build();

            Page<CampaignDeviceMap> campaignDevices = new PageImpl<>(List.of(campaignDeviceMap1, campaignDeviceMap2));
            PageRequest pageRequest = PageRequest.of(campaignStatusDetail.offset(), campaignStatusDetail.size());

            given(campaignRepository.findByCampaignName(resourceOwner.getCompanyId(), campaignStatusDetail.deploymentId()))
                    .willReturn(targetCampaign);
            given(campaignRepository.findCampaignStatusByCampaign(targetCampaign))
                    .willReturn(campaignStatusAggregation);
            given(campaignDeviceMapRepository.findByCampaignDevices(targetCampaign, pageRequest))
                    .willReturn(campaignDevices);

            FoundCampaignStatusDetail found = fotaService.getCampaignStatusDetail(resourceOwner, campaignStatusDetail);

            assertThat(found).isNotNull();


            verify(campaignRepository, times(1))
                    .findByCampaignName(resourceOwner.getCompanyId(), campaignStatusDetail.deploymentId());
            verify(campaignRepository, times(1)).findCampaignStatusByCampaign(targetCampaign);
        }

        @Test
        @DisplayName("200 OK - appendStatus = false")
        void getCampaignStatusDetailTest200_appendStatus_false() {
            Campaign targetCampaign = Campaign.builder().id(1L).name("FOTA-27").build();

            CampaignRequestDto.CampaignStatusDetail campaignStatusDetail = new CampaignRequestDto.CampaignStatusDetail(
                    "FOTA-27",
                    false,
                    0,
                    10
            );
            CampaignStatusAggregation campaignStatusAggregation = new CampaignStatusAggregation(
                    "FOTA-27",
                    CampaignStatus.ACTIVE,
                    15L,
                    1,
                    2,
                    3,
                    4,
                    5,
                    now()
            );

            given(campaignRepository.findByCampaignName(resourceOwner.getCompanyId(), campaignStatusDetail.deploymentId()))
                    .willReturn(targetCampaign);
            given(campaignRepository.findCampaignStatusByCampaign(targetCampaign))
                    .willReturn(campaignStatusAggregation);
            given(campaignDeviceMapRepository.findByCampaignDevices(targetCampaign, null))
                    .willReturn(null);

            FoundCampaignStatusDetail found = fotaService.getCampaignStatusDetail(resourceOwner, campaignStatusDetail);

            assertThat(found).isNotNull();
            assertThat(found.getData().getCompletedOn()).isEqualTo(campaignStatusAggregation.getCompletedOn());
            assertThat(found.getData().getCompleted()).isEqualTo(campaignStatusAggregation.getCompleted());
            assertThat(found.getData().getDownloading()).isEqualTo(campaignStatusAggregation.getDownloading());
            assertThat(found.getData().getAwaitingInstall()).isEqualTo(campaignStatusAggregation.getAwaitingInstall());
            assertThat(found.getData().getScheduled()).isEqualTo(campaignStatusAggregation.getScheduled());
            assertThat(found.getData().getFailed()).isEqualTo(campaignStatusAggregation.getFailed());
            assertThat(found.getData().getTotalDevices()).isEqualTo(campaignStatusAggregation.getTotalDevices());

            verify(campaignRepository, times(1)).findByCampaignName(resourceOwner.getCompanyId(), campaignStatusDetail.deploymentId());
            verify(campaignRepository, times(1)).findCampaignStatusByCampaign(targetCampaign);
            verify(campaignDeviceMapRepository, never()).findByCampaignDevices(targetCampaign, null);
        }

        @Test
        @DisplayName("404 Not Found - Campaign")
        void getCampaignStatusDetailTest404_notFound_campaign() {
            given(campaignRepository.findByCampaignName(resourceOwner.getCompanyId(), campaignStatusDetail.deploymentId()))
                    .willReturn(null);

            assertThatThrownBy(() -> fotaService.getCampaignStatusDetail(resourceOwner, campaignStatusDetail))
                    .isInstanceOf(BizException.class)
                    .hasMessage(FOTACrudErrorCode.CAMPAIGN_NOT_FOUND.getMsg());

            verify(campaignRepository, times(1))
                    .findByCampaignName(resourceOwner.getCompanyId(), campaignStatusDetail.deploymentId());
        }
    }

    @Nested
    @DisplayName("cancelCampaign test")
    class CancelCampaignTest {

        ResourceOwnerDto resourceOwner = new ResourceOwnerDto(
                1L,
                "saved_resourceOwnerId_1",
                "saved_email_1",
                1L
        );

        CampaignRequestDto.CancelCampaign cancelCampaign = new CampaignRequestDto.CancelCampaign("FOTA-27");

        @Test
        @DisplayName("200 OK")
        void cancelCampaignTest200() {
            Optional<Campaign> targetCampaign = Optional.of(Campaign.builder().id(1L).name("FOTA-27").companyId(1L).build());
            CampaignDeviceMap campaignDeviceMap1 = CampaignDeviceMap.builder()
                    .campaign(targetCampaign.get())
                    .device(Device.builder()
                            .deviceName("EF501_EF501ANLKBA159")
                            .serialNumber("EF501ANLKBA159")
                            .status(DeviceStatus.ACTIVE)
                            .deviceDetail(DeviceDetail.builder().modelName("EF501").build())
                            .build())
                    .build();

            CampaignDeviceMap campaignDeviceMap2 = CampaignDeviceMap.builder()
                    .campaign(targetCampaign.get())
                    .device(Device.builder()
                            .deviceName("EF501_EF501ANLKBA260")
                            .serialNumber("EF501ANLKBA260")
                            .status(DeviceStatus.ACTIVE)
                            .deviceDetail(DeviceDetail.builder().modelName("EF501").build())
                            .build())
                    .build();

            given(campaignRepository.findByName(cancelCampaign.deploymentId())).willReturn(targetCampaign);
            given(campaignDeviceMapRepository.findByCampaign(targetCampaign.get())).willReturn(List.of(campaignDeviceMap1, campaignDeviceMap2));
            given(resourceOwnerService.findByResourceOwnerId(resourceOwner.getResourceOwnerId())).willReturn(resourceOwner);

            doNothing().when(campaignDeviceMapRepository).deleteByCampaign(targetCampaign.get());
            doNothing().when(campaignPackageMapRepository).deleteByCampaign(targetCampaign.get());
            doNothing().when(campaignDeviceGroupMapRepository).deleteByCampaign(targetCampaign.get());
            doNothing().when(campaignDeviceTagMapRepository).deleteByCampaign(targetCampaign.get());
            doNothing().when(campaignDeviceMapRepository).flush();
            doNothing().when(campaignPackageMapRepository).flush();
            doNothing().when(campaignDeviceGroupMapRepository).flush();
            doNothing().when(campaignDeviceTagMapRepository).flush();
            doNothing().when(campaignRepository).deleteById(targetCampaign.get().getId());

            CancelledCampaign cancelledCampaign = fotaService.cancelCampaign(resourceOwner.getResourceOwnerId(), cancelCampaign.deploymentId());

            assertThat(cancelledCampaign).isNotNull();
            assertThat(cancelledCampaign.getDeploymentId()).isEqualTo(cancelCampaign.deploymentId());

            verify(campaignRepository, times(1)).findByName(cancelCampaign.deploymentId());
            verify(campaignDeviceMapRepository, times(1)).findByCampaign(targetCampaign.get());
            verify(campaignPackageMapRepository, times(1)).deleteByCampaign(targetCampaign.get());
            verify(campaignDeviceGroupMapRepository, times(1)).deleteByCampaign(targetCampaign.get());
            verify(campaignDeviceTagMapRepository, times(1)).deleteByCampaign(targetCampaign.get());
            verify(campaignDeviceMapRepository, times(1)).flush();
            verify(campaignPackageMapRepository, times(1)).flush();
            verify(campaignDeviceGroupMapRepository, times(1)).flush();
            verify(campaignDeviceTagMapRepository, times(1)).flush();
            verify(campaignRepository, times(1)).deleteById(targetCampaign.get().getId());

        }

        @Test
        @DisplayName("404 Not Found - Campaign")
        void cancelCampaignTest404_notFound_campaign() {
            given(campaignRepository.findByName(cancelCampaign.deploymentId()))
                    .willThrow(new BizException(FOTACrudErrorCode.CAMPAIGN_NOT_FOUND));

            assertThatThrownBy(() -> fotaService.cancelCampaign(resourceOwner.getResourceOwnerId(), cancelCampaign.deploymentId()))
                    .isInstanceOf(BizException.class)
                    .hasMessage(FOTACrudErrorCode.CAMPAIGN_NOT_FOUND.getMsg());

            verify(campaignRepository, times(1)).findByName(cancelCampaign.deploymentId());

        }

        @Test
        @DisplayName("400 Bad Request - Attempted to cancel campaign with not valid user")
        void cancelCampaignTest400_attemptedCancelCampaignWithNotValidUser() {
            Campaign otherCompany = Campaign.builder().id(1L).name("FOTA-27").companyId(2L).build();

            given(campaignRepository.findByName(cancelCampaign.deploymentId())).willReturn(Optional.of(otherCompany));
            given(resourceOwnerService.findByResourceOwnerId(resourceOwner.getResourceOwnerId())).willReturn(resourceOwner);

            assertThatThrownBy(() -> fotaService.cancelCampaign(resourceOwner.getResourceOwnerId(), cancelCampaign.deploymentId()))
                    .isInstanceOf(BizException.class)
                    .hasMessage(FOTACrudErrorCode.ATTEMPTED_CANCEL_CAMPAIGN_WITH_NOT_VALID_USER.getMsg());

            verify(campaignRepository, times(1)).findByName(cancelCampaign.deploymentId());
        }
    }

    @Nested
    @DisplayName("getFOTAReadyDevice test")
    class GetFOTAReadyDeviceTest {

        ResourceOwnerDto resourceOwner = new ResourceOwnerDto(
                1L,
                "saved_resourceOwnerId_1",
                "saved_email_1",
                1L
        );

        CampaignRequestDto.FOTAReadyDevice fotaReadyDevice = new CampaignRequestDto.FOTAReadyDevice(
                "FOTA-27",
                "EF501",
                0,
                10
        );

        FOTAReadyDeviceContent content1 = new FOTAReadyDeviceContent(
                "EF501",
                "EF501ANLKBA159",
                1,
                now(),
                "20230101_R1.00",
                "10"
        );

        FOTAReadyDeviceContent content2 = new FOTAReadyDeviceContent(
                "EF501",
                "EF501ANLKBA260",
                1,
                now(),
                "20230102_R1.00",
                "10"
        );

        @Test
        @DisplayName("200 OK")
        void getFOTAReadyDeviceTest200() {
            Page<FOTAReadyDeviceContent> fotaReadyDeviceContent = new PageImpl<>(List.of(content1, content2), PageRequest.of(0, 10), 2L);
            given(deviceRepository.findFOTAReadyDevice(resourceOwner.getCompanyId(), fotaReadyDevice))
                    .willReturn(fotaReadyDeviceContent);

            FotaReadyDevice found = fotaService.getFOTAReadyDevice(resourceOwner.getCompanyId(), fotaReadyDevice);

            assertThat(found).isNotNull();

            verify(deviceRepository, times(1)).findFOTAReadyDevice(resourceOwner.getCompanyId(), fotaReadyDevice);
        }
    }
}