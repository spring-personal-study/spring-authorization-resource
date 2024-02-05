package com.bos.resource.integration.device;

import com.bos.resource.app.device.controller.DeviceController;
import com.bos.resource.app.resourceowner.exception.ResourceOwnerErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("DeviceController Test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = "spring.profiles.active=test")
public class DeviceControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .apply(springSecurity())
                .build();
    }

    @Nested
    @DisplayName("/v1/asset")
    class AssetTest {

        @Test
        @DisplayName("200 OK")
        public void testAsset() throws Exception {
//            final String username = "dev_admin";
            final String username = "fake_user1";
            final String message = "success";
            final String model = "HF550";
            final String serialNumber = "HF550XANLCBA001";

            mockMvc.perform(get("/v1/asset")
                            .with(jwt()
                                    .jwt(jwt -> jwt.claim(StandardClaimNames.SUB, username)))
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(handler().handlerType(DeviceController.class))
                    .andExpect(handler().methodName("asset"))
                    .andExpect(jsonPath("$.data").isNotEmpty())
                    .andExpect(jsonPath("$.data.message").value(message))
                    .andExpect(jsonPath("$.data.assets").isNotEmpty())
                    .andExpect(jsonPath("$.data.assets[0].model").value(model))
                    .andExpect(jsonPath("$.data.assets[0].serialNumber").value(serialNumber));
        }

        @Test
        @DisplayName("404 Not Found")
        public void testAsset_fail_notExistsUser() throws Exception {
            final String username = "not_exists_user1";

            mockMvc.perform(get("/v1/asset")
                            .with(jwt()
                                    .jwt(jwt -> jwt.claim(StandardClaimNames.SUB, username)))
                    )
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(handler().handlerType(DeviceController.class))
                    .andExpect(handler().methodName("asset"))
                    .andExpect(jsonPath("$.msg").value(ResourceOwnerErrorCode.RESOURCE_OWNER_NOT_FOUND.getMsg()))
                    .andExpect(jsonPath("$.httpCode").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.internalCode").value(ResourceOwnerErrorCode.RESOURCE_OWNER_NOT_FOUND.getBizCode()))
                    .andExpect(jsonPath("$.timestamp").isNotEmpty());
        }
    }
}
