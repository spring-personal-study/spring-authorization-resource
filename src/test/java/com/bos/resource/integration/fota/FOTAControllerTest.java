package com.bos.resource.integration.fota;

import com.bos.resource.app.fota.controller.CampaignController;
import com.bos.resource.app.fota.model.constants.enums.NotificationType;
import com.bos.resource.app.fota.model.dto.CampaignRequestDto;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("CampaignController Test")
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
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

        CampaignRequestDto.CreateCampaignDto.CampaignDevice devices = new CampaignRequestDto.CreateCampaignDto.CampaignDevice(
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

            mockMvc.perform(post("/v1/deployments")
                            .with(jwt()
                                    .jwt(jwt -> jwt.claim(StandardClaimNames.SUB, username))
                            )
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createCampaignDto)))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Transactional
        @Rollback
        @Test
        @DisplayName("404 Not Found - Firmware does not exists")
        public void testCreateCampaign_fail_firmware_notFound() throws Exception {
            String username = "fake_user1";
            devices = new CampaignRequestDto.CreateCampaignDto.CampaignDevice(
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
                    .andExpect(status().isNotFound());
        }

        @Transactional
        @Rollback
        @Test
        @DisplayName("404 Not Found - Firmware does not exists")
        public void testCreateCampaign_fail_user_notFound() throws Exception {
            String username = "not_exists_user1";
            devices = new CampaignRequestDto.CreateCampaignDto.CampaignDevice(
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
                    .andExpect(status().isNotFound());
        }
    }

}
