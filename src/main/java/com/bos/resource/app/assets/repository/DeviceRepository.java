package com.bos.resource.app.assets.repository;

import com.bos.resource.app.assets.model.entity.Device;
import com.bos.resource.app.assets.repository.querydsl.QDeviceRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long>, QDeviceRepository {
}
