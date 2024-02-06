package com.bos.resource.slice.repository;

import com.bos.resource.app.device.model.entity.Device;
import com.bos.resource.app.device.repository.device.DeviceRepository;
import com.bos.resource.config.DatabaseConfig;
import com.bos.resource.config.P6SpyFormatter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Unit Test - Device Repository")
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({DatabaseConfig.class, P6SpyFormatter.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class DeviceRepositoryTest {

    @Autowired
    private DeviceRepository deviceRepository;

    @Test
    @DisplayName("Create Device Entity - OK")
    void findById() {
        Optional<Device> byId = deviceRepository.findById(1L);
        assertThat(byId).isNotNull();
        assertThat(byId.isPresent()).isTrue();
        assertThat(byId.get().getId()).isEqualTo(1L);
    }

}
