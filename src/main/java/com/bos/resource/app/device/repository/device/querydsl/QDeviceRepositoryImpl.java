package com.bos.resource.app.device.repository.device.querydsl;


import com.bos.resource.app.device.model.dto.DeviceResponseDto;
import com.bos.resource.app.device.model.entity.QDevice;
import com.bos.resource.app.device.model.entity.QDeviceDetail;
import com.bos.resource.app.resourceowner.model.entity.QResourceOwner;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class QDeviceRepositoryImpl implements QDeviceRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<DeviceResponseDto.DeviceDto> findAssetByEnrolledUser(Long userId) {
        QDevice qDevice = QDevice.device;
        QDeviceDetail qDeviceDetail = QDeviceDetail.deviceDetail;
        QResourceOwner qResourceOwner = QResourceOwner.resourceOwner;

        return jpaQueryFactory
                .select(Projections.constructor(
                        DeviceResponseDto.DeviceDto.class,
                        qDeviceDetail.modelName,
                        QDevice.device.serialNumber))
                .from(qDevice)
                .innerJoin(qDeviceDetail).on(qDeviceDetail.eq(qDevice.deviceDetail))
                .innerJoin(qResourceOwner).on(qResourceOwner.eq(qDevice.resourceOwner))
                .fetchJoin()
                .where(qResourceOwner.id.eq(userId))
                .fetch();
    }
}
