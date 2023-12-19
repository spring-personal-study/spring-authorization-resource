package com.bos.resource.app.fota.repository;

import com.bos.resource.app.device.model.entity.SupportModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupportModelRepository extends JpaRepository<SupportModel, Long> {
    SupportModel findByName(String model);
}
