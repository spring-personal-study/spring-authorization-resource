package com.bos.resource.slice.repository;

import com.bos.resource.app.device.model.entity.Device;
import com.bos.resource.app.device.repository.device.DeviceRepository;
import com.bos.resource.config.DatabaseConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(locations = "classpath:application_db.yml")
@Import({DatabaseConfig.class})
public class DeviceRepositoryTest {

    @Autowired
    DeviceRepository deviceRepository;

    @Test
    @DisplayName("Create Device Entity - OK")
    void findById() {
        Optional<Device> byId = deviceRepository.findById(1L);
        assertThat(byId).isNotNull();
        assertThat(byId.isPresent()).isTrue();
        assertThat(byId.get().getId()).isEqualTo(1L);
    }

}
