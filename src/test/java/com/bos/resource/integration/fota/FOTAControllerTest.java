package com.bos.resource.integration.fota;

import com.bos.resource.app.common.apiresponse.ApiSuccessMessage;
import com.bos.resource.app.fota.controller.CampaignController;
import com.bos.resource.app.fota.exception.FOTACrudErrorCode;
import com.bos.resource.app.fota.model.constants.enums.NotificationType;
import com.bos.resource.app.fota.model.dto.CampaignRequestDto;
import com.bos.resource.app.fota.model.dto.CampaignRequestDto.CreateCampaignDto.CampaignDevice;
import com.bos.resource.app.resourceowner.exception.ResourceOwnerErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.time.LocalDateTime;
import java.util.List;

import static com.bos.resource.app.fota.model.constants.strings.FirmwareUpdateTypeConstants.LATEST_VALUE;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Acceptance Test - CampaignController")
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class FOTAControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .apply(springSecurity())
                .build();
    }

    @Nested
    @DisplayName("/v1/notification - new artifact auto update")
    class NotificationTest {
        final String notificationType = NotificationType.NEW_ARTIFACT_AUTO_UPDATE.getName();
        Integer offset = 0;
        Integer limit = 100;

        CampaignRequestDto.Notification.PeriodParams periodParams = new CampaignRequestDto.Notification.PeriodParams(
                LocalDateTime.parse("2023-01-01T00:00:00"),
                LocalDateTime.parse("2025-12-31T23:59:59")
        );
        CampaignRequestDto.Notification notification = new CampaignRequestDto.Notification(
                notificationType,
                periodParams,
                offset,
                limit
        );
        String username = "fake_user1";

        @Test
        @DisplayName("200 OK")
        public void testNotification_isOk() throws Exception {
            mockMvc.perform(post("/v1/notification")
                            .with(jwt()
                                    .jwt(jwt -> jwt.claim(StandardClaimNames.SUB, username))
                            )
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(notification)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.head").exists())
                    .andExpect(jsonPath("$.head.page").value(0))
                    .andExpect(jsonPath("$.head.offset").value(0))
                    .andExpect(jsonPath("$.head.limit").value(100))
                    .andExpect(jsonPath("$.head.total").value(3))
                    .andExpect(jsonPath("$.notifications").exists())
                    .andExpect(jsonPath("$.notificationsErrors").doesNotExist());
        }

        @Test
        @DisplayName("404 Not Found - Not Existed User")
        public void testNotification_cannot() throws Exception {
            username = "not_exists_user1";
            mockMvc.perform(post("/v1/notification")
                            .with(jwt()
                                    .jwt(jwt -> jwt.claim(StandardClaimNames.SUB, username))
                            )
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(notification)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(handler().handlerType(CampaignController.class))
                    .andExpect(handler().methodName("notification"))
                    .andExpect(jsonPath("$.msg").value(ResourceOwnerErrorCode.RESOURCE_OWNER_NOT_FOUND.getMsg()))
                    .andExpect(jsonPath("$.httpCode").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.internalCode").value(ResourceOwnerErrorCode.RESOURCE_OWNER_NOT_FOUND.getBizCode()))
                    .andExpect(jsonPath("$.timestamp").isNotEmpty());
        }
    }

    @Nested
    @DisplayName("/v1/deployments (create campaign)")
    class CreateCampaignTest {

        CampaignRequestDto.CreateCampaignDto.CampaignProfile profile = new CampaignRequestDto.CreateCampaignDto.CampaignProfile(LATEST_VALUE, null);
        CampaignRequestDto.CreateCampaignDto.CampaignRule.InstallRule install = new CampaignRequestDto.CreateCampaignDto.CampaignRule.InstallRule("2023-01-31T00:00:00", "19:25", "21:25", false);
        CampaignRequestDto.CreateCampaignDto.CampaignRule rule = new CampaignRequestDto.CreateCampaignDto.CampaignRule(install);

        CampaignDevice devices = new CampaignDevice(
                "HF550",
                List.of("HF550XANLCBA001", "HF550XANLCBA002")
        );

        CampaignRequestDto.CreateCampaignDto createCampaignDto = new CampaignRequestDto.CreateCampaignDto(profile, rule, devices);

        @Transactional
        @Rollback
        @Test
        @DisplayName("200 OK")
        public void testCreateCampaign_isOk() throws Exception {
            String username = "fake_user1";
            String campaignNamePrefix = "FOTA-";

            mockMvc.perform(post("/v1/deployments")
                            .with(jwt()
                                    .jwt(jwt -> jwt.claim(StandardClaimNames.SUB, username))
                            )
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createCampaignDto)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.deploymentId").isNotEmpty())
                    .andExpect(jsonPath("$.deploymentId").isString())
                    .andExpect(jsonPath("$.deploymentId").value(startsWith(campaignNamePrefix)))
                    .andExpect(jsonPath("$.action").value(HttpStatus.CREATED.name()))
                    .andExpect(jsonPath("$.message").value(ApiSuccessMessage.CREATE_DEPLOYMENT_SUCCESS))
                    .andExpect(jsonPath("$.code").value(String.valueOf(HttpStatus.OK.value())))
                    .andExpect(jsonPath("$.timestamp").isNotEmpty());
        }

        @Transactional
        @Rollback
        @Test
        @DisplayName("404 Not Found - Firmware does not exists")
        public void testCreateCampaign_fail_firmware_notFound() throws Exception {
            String username = "fake_user1";
            devices = new CampaignDevice(
                    "EF501",
                    List.of("EF501ANCLBA192", "EF501ANCLBA193", "EF501ANCLBA194")
            );
            createCampaignDto = new CampaignRequestDto.CreateCampaignDto(profile, rule, devices);

            mockMvc.perform(post("/v1/deployments")
                            .with(jwt()
                                    .jwt(jwt -> jwt.claim(StandardClaimNames.SUB, username))
                            )
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createCampaignDto)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(handler().handlerType(CampaignController.class))
                    .andExpect(handler().methodName("campaigns"))
                    .andExpect(jsonPath("$.msg").value(FOTACrudErrorCode.FIRMWARE_NOT_FOUND.getMsg()))
                    .andExpect(jsonPath("$.httpCode").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.internalCode").value(FOTACrudErrorCode.FIRMWARE_NOT_FOUND.getBizCode()))
                    .andExpect(jsonPath("$.timestamp").isNotEmpty());
        }

        @Transactional
        @Rollback
        @Test
        @DisplayName("404 Not Found - User does not exist")
        public void testCreateCampaign_fail_user_notFound() throws Exception {
            String username = "not_exists_user1";
            devices = new CampaignDevice(
                    "EF501",
                    List.of("EF501ANCLBA192", "EF501ANCLBA193", "EF501ANCLBA194")
            );
            createCampaignDto = new CampaignRequestDto.CreateCampaignDto(profile, rule, devices);

            mockMvc.perform(post("/v1/deployments")
                            .with(jwt()
                                    .jwt(jwt -> jwt.claim(StandardClaimNames.SUB, username))
                            )
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createCampaignDto)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(handler().handlerType(CampaignController.class))
                    .andExpect(handler().methodName("campaigns"))
                    .andExpect(jsonPath("$.msg").value(ResourceOwnerErrorCode.RESOURCE_OWNER_NOT_FOUND.getMsg()))
                    .andExpect(jsonPath("$.httpCode").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.internalCode").value(ResourceOwnerErrorCode.RESOURCE_OWNER_NOT_FOUND.getBizCode()))
                    .andExpect(jsonPath("$.timestamp").isNotEmpty());
        }
    }

    @Nested
    @DisplayName("/v1/deployments/status")
    class CampaignStatusTest {
        LocalDateTime startDate = LocalDateTime.parse("2023-12-01T00:00:00");
        LocalDateTime endDate = LocalDateTime.parse("2023-12-31T23:59:59");

        CampaignRequestDto.CampaignStatus campaignStatus = new CampaignRequestDto.CampaignStatus(
                List.of("FOTA-1", "FOTA-2", "FOTA-3"),
                "ALL",
                startDate,
                endDate
        );
        String username = "fake_user1";

        @Test
        @DisplayName("200 OK")
        public void testCampaignStatus_isOk() throws Exception {
            mockMvc.perform(post("/v1/deployments/status")
                            .with(jwt()
                                    .jwt(jwt -> jwt.claim(StandardClaimNames.SUB, username))
                            )
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(campaignStatus)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").exists())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data").isNotEmpty())
                    .andExpect(jsonPath("$.data[0].deploymentId").isNotEmpty())
                    .andExpect(jsonPath("$.data[0].deploymentId").isString())
                    .andExpect(jsonPath("$.data[0].deploymentStatus").isNotEmpty())
                    .andExpect(jsonPath("$.data[0].deploymentStatus").isString())
                    .andExpect(jsonPath("$.data[0].totalDevices").exists())
                    .andExpect(jsonPath("$.data[0].totalDevices").isNumber())
                    .andExpect(jsonPath("$.data[0].scheduled").exists())
                    .andExpect(jsonPath("$.data[0].downloading").exists())
                    .andExpect(jsonPath("$.data[0].awaitingInstall").exists())
                    .andExpect(jsonPath("$.data[0].completed").exists())
                    .andExpect(jsonPath("$.data[0].failed").exists())
                    .andExpect(jsonPath("$.data[0].completedOn").exists());
        }

        @Test
        @DisplayName("400 Bad Request - exceeded date range")
        public void testCampaignStatus_failed_dateRangeExceeded() throws Exception {
            LocalDateTime endDate = LocalDateTime.parse("2030-12-31T23:59:59");

            CampaignRequestDto.CampaignStatus campaignStatus = new CampaignRequestDto.CampaignStatus(
                    List.of("FOTA-1", "FOTA-2", "FOTA-3"),
                    "ALL",
                    startDate,
                    endDate
            );

            mockMvc.perform(post("/v1/deployments/status")
                            .with(jwt()
                                    .jwt(jwt -> jwt.claim(StandardClaimNames.SUB, username))
                            )
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(campaignStatus)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.msg").value(FOTACrudErrorCode.DATE_RANGE_EXCEEDED.getMsg()))
                    .andExpect(jsonPath("$.httpCode").value(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath("$.internalCode").value(FOTACrudErrorCode.DATE_RANGE_EXCEEDED.getBizCode()))
                    .andExpect(jsonPath("$.timestamp").isNotEmpty());
        }

        @Test
        @DisplayName("404 Not Found - Not Existed User")
        public void testCampaignStatus_cannot() throws Exception {
            username = "not_exists_user1";
            mockMvc.perform(post("/v1/deployments/status")
                            .with(jwt()
                                    .jwt(jwt -> jwt.claim(StandardClaimNames.SUB, username))
                            )
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(campaignStatus)))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("/v1/deployments/detail")
    class CampaignStatusDetailTest {
        CampaignRequestDto.CampaignStatusDetail campaignStatusDetail = new CampaignRequestDto.CampaignStatusDetail(
                "FOTA-1",
                true,
                0,
                10
        );
        String username = "fake_user1";

        @Test
        @DisplayName("200 OK - appendStatus: true")
        public void testCampaignStatusDetail_isOk_appendStatusTrue() throws Exception {

            mockMvc.perform(post("/v1/deployments/detail")
                            .with(jwt()
                                    .jwt(jwt -> jwt.claim(StandardClaimNames.SUB, username))
                            )
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(campaignStatusDetail)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.deploymentId").isNotEmpty())
                    .andExpect(jsonPath("$.data.deploymentId").isString())
                    .andExpect(jsonPath("$.data.deploymentStatus").isNotEmpty())
                    .andExpect(jsonPath("$.data.deploymentStatus").isString())
                    .andExpect(jsonPath("$.data.totalDevices").exists())
                    .andExpect(jsonPath("$.data.totalDevices").isNumber())
                    .andExpect(jsonPath("$.data.scheduled").exists())
                    .andExpect(jsonPath("$.data.downloading").exists())
                    .andExpect(jsonPath("$.data.awaitingInstall").exists())
                    .andExpect(jsonPath("$.data.completed").exists())
                    .andExpect(jsonPath("$.data.failed").exists())
                    .andExpect(jsonPath("$.data.completedOn").exists())
                    .andExpect(jsonPath("$.data.devices").exists())
                    .andExpect(jsonPath("$.data.devices").isArray())
                    .andExpect(jsonPath("$.data.devices[0].model").exists())
                    .andExpect(jsonPath("$.data.devices[0].serialNumber").exists())
                    .andExpect(jsonPath("$.data.devices[0].status").exists())
                    .andExpect(jsonPath("$.data.devices[0].message").exists())
                    .andExpect(jsonPath("$.data.devices[0].completionTime").exists())
                    .andExpect(jsonPath("$.head").exists())
                    .andExpect(jsonPath("$.head.page").value(0))
                    .andExpect(jsonPath("$.head.offset").value(campaignStatusDetail.offset()))
                    .andExpect(jsonPath("$.head.limit").value(campaignStatusDetail.size()))
                    .andExpect(jsonPath("$.head.total").exists());
        }

        @Test
        @DisplayName("200 OK - appendStatus: false")
        public void testCampaignStatusDetail_isOk_appendStatusFalse() throws Exception {
            CampaignRequestDto.CampaignStatusDetail campaignStatusDetail = new CampaignRequestDto.CampaignStatusDetail(
                    "FOTA-1",
                    false,
                    0,
                    10
            );

            mockMvc.perform(post("/v1/deployments/detail")
                            .with(jwt()
                                    .jwt(jwt -> jwt.claim(StandardClaimNames.SUB, username))
                            )
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(campaignStatusDetail)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.deploymentId").isNotEmpty())
                    .andExpect(jsonPath("$.data.deploymentId").isString())
                    .andExpect(jsonPath("$.data.deploymentStatus").isNotEmpty())
                    .andExpect(jsonPath("$.data.deploymentStatus").isString())
                    .andExpect(jsonPath("$.data.totalDevices").exists())
                    .andExpect(jsonPath("$.data.totalDevices").isNumber())
                    .andExpect(jsonPath("$.data.scheduled").exists())
                    .andExpect(jsonPath("$.data.downloading").exists())
                    .andExpect(jsonPath("$.data.awaitingInstall").exists())
                    .andExpect(jsonPath("$.data.completed").exists())
                    .andExpect(jsonPath("$.data.failed").exists())
                    .andExpect(jsonPath("$.data.completedOn").exists())
                    .andExpect(jsonPath("$.data.devices").doesNotExist())
                    .andExpect(jsonPath("$.head").doesNotExist());
        }

        @Test
        @DisplayName("404 Not Found - Not Existed Campaign")
        public void testCampaignStatusDetail_notFoundCampaignByName() throws Exception {
            CampaignRequestDto.CampaignStatusDetail campaignStatusDetail = new CampaignRequestDto.CampaignStatusDetail(
                    "NOT_EXISTS_CAMPAIGN_NAME_59",
                    true,
                    0,
                    10
            );

            mockMvc.perform(post("/v1/deployments/detail")
                            .with(jwt()
                                    .jwt(jwt -> jwt.claim(StandardClaimNames.SUB, username))
                            )
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(campaignStatusDetail)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.msg").value(FOTACrudErrorCode.CAMPAIGN_NOT_FOUND.getMsg()))
                    .andExpect(jsonPath("$.httpCode").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.internalCode").value(FOTACrudErrorCode.CAMPAIGN_NOT_FOUND.getBizCode()))
                    .andExpect(jsonPath("$.timestamp").isNotEmpty());
        }

        @Test
        @DisplayName("404 Not Found - Not Existed User")
        public void testCampaignStatusDetail_fail_notFoundUser() throws Exception {
            username = "not_exists_user1";
            mockMvc.perform(post("/v1/deployments/detail")
                            .with(jwt()
                                    .jwt(jwt -> jwt.claim(StandardClaimNames.SUB, username))
                            )
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(campaignStatusDetail)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.msg").value(ResourceOwnerErrorCode.RESOURCE_OWNER_NOT_FOUND.getMsg()))
                    .andExpect(jsonPath("$.httpCode").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.internalCode").value(ResourceOwnerErrorCode.RESOURCE_OWNER_NOT_FOUND.getBizCode()))
                    .andExpect(jsonPath("$.timestamp").isNotEmpty());
        }

    }

    @Nested
    @DisplayName("/v1/deployments/cancel")
    class CancelCampaignTest {
        CampaignRequestDto.CancelCampaign cancelCampaign = new CampaignRequestDto.CancelCampaign("FOTA-3");
        String username = "fake_user1";

        @Transactional
        @Rollback
        @Test
        @DisplayName("200 OK")
        public void testCancelCampaign_isOk() throws Exception {
            mockMvc.perform(post("/v1/deployments/cancel")
                            .with(jwt()
                                    .jwt(jwt -> jwt.claim(StandardClaimNames.SUB, username))
                            )
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cancelCampaign)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.deploymentId").isNotEmpty())
                    .andExpect(jsonPath("$.deploymentId").isString())
                    .andExpect(jsonPath("$.action").value("canceled"))
                    .andExpect(jsonPath("$.message").value(ApiSuccessMessage.CANCEL_DEPLOYMENT_SUCCESS))
                    .andExpect(jsonPath("$.code").value("success"))
                    .andExpect(jsonPath("$.timestamp").isNotEmpty());
        }

        @Test
        @DisplayName("400 Bad Request - Not Permitted User")
        public void testCancelCampaign_failed_notPermitUser() throws Exception {
            String username = "fake_user3";
            mockMvc.perform(post("/v1/deployments/cancel")
                            .with(jwt()
                                    .jwt(jwt -> jwt.claim(StandardClaimNames.SUB, username))
                            )
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cancelCampaign)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.msg").value(FOTACrudErrorCode.ATTEMPTED_CANCEL_CAMPAIGN_WITH_NOT_VALID_USER.getMsg()))
                    .andExpect(jsonPath("$.httpCode").value(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath("$.internalCode").value(FOTACrudErrorCode.ATTEMPTED_CANCEL_CAMPAIGN_WITH_NOT_VALID_USER.getBizCode()))
                    .andExpect(jsonPath("$.timestamp").isNotEmpty());
        }


        @Test
        @DisplayName("404 Not Found - Not Existed Campaign Name")
        public void testCancelCampaign_failed_notExistCampaign() throws Exception {
            CampaignRequestDto.CancelCampaign cancelCampaign = new CampaignRequestDto.CancelCampaign("NOT_EXISTS_CAMPAIGN_NAME_59");
            mockMvc.perform(post("/v1/deployments/cancel")
                            .with(jwt()
                                    .jwt(jwt -> jwt.claim(StandardClaimNames.SUB, username))
                            )
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cancelCampaign)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.msg").value(FOTACrudErrorCode.CAMPAIGN_NOT_FOUND.getMsg()))
                    .andExpect(jsonPath("$.httpCode").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.internalCode").value(FOTACrudErrorCode.CAMPAIGN_NOT_FOUND.getBizCode()))
                    .andExpect(jsonPath("$.timestamp").isNotEmpty());
        }

        @Test
        @DisplayName("404 Not Found - Not Existed User")
        public void testCancelCampaign_failed_notExistUser() throws Exception {
            username = "not_exists_user1";
            mockMvc.perform(post("/v1/deployments/cancel")
                            .with(jwt()
                                    .jwt(jwt -> jwt.claim(StandardClaimNames.SUB, username))
                            )
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cancelCampaign)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.msg").value(ResourceOwnerErrorCode.RESOURCE_OWNER_NOT_FOUND.getMsg()))
                    .andExpect(jsonPath("$.httpCode").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.internalCode").value(ResourceOwnerErrorCode.RESOURCE_OWNER_NOT_FOUND.getBizCode()))
                    .andExpect(jsonPath("$.timestamp").isNotEmpty());
        }
    }

    @Nested
    @DisplayName("/v1/devices")
    class DevicesTest {
        CampaignRequestDto.FOTAReadyDevice device = new CampaignRequestDto.FOTAReadyDevice("1", "1", 0, 10);
        String username = "fake_user1";

        @Test
        @DisplayName("200 OK")
        public void testDevices_isOk() throws Exception {
            mockMvc.perform(post("/v1/devices")
                            .with(jwt()
                                    .jwt(jwt -> jwt.claim(StandardClaimNames.SUB, username))
                            )
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(device)))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("404 Not Found - Not Existed User")
        public void testDevices_fail_notFoundUser() throws Exception {
            username = "not_exists_user1";
            mockMvc.perform(post("/v1/devices")
                            .with(jwt()
                                    .jwt(jwt -> jwt.claim(StandardClaimNames.SUB, username))
                            )
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(device)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.msg").value(ResourceOwnerErrorCode.RESOURCE_OWNER_NOT_FOUND.getMsg()))
                    .andExpect(jsonPath("$.httpCode").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.internalCode").value(ResourceOwnerErrorCode.RESOURCE_OWNER_NOT_FOUND.getBizCode()))
                    .andExpect(jsonPath("$.timestamp").isNotEmpty());
        }
    }


}
