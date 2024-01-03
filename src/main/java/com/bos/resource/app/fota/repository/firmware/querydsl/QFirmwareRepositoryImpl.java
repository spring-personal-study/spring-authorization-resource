package com.bos.resource.app.fota.repository.firmware.querydsl;

import com.bos.resource.app.fota.model.entity.Firmware;
import com.bos.resource.app.fota.model.entity.QFirmware;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

@RequiredArgsConstructor
public class QFirmwareRepositoryImpl implements QFirmwareRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Firmware> findByModelPaging(String model, String version, Pageable pageable) {
        QFirmware qFirmware = QFirmware.firmware;

        List<Firmware> firmwares = queryFactory.selectFrom(qFirmware)
                .where(qFirmware.model.eq(model), qFirmware.version.eq(version))
                .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(qFirmware.count())
                .from(qFirmware)
                .where(qFirmware.model.eq(model), qFirmware.version.eq(version));

        return PageableExecutionUtils.getPage(firmwares, pageable, countQuery::fetchOne);
    }

    @Override
    public List<Firmware> findByModel(String model, String version) {
        QFirmware qFirmware = QFirmware.firmware;
        return queryFactory.selectFrom(qFirmware)
                .where(qFirmware.model.eq(model), qFirmware.version.eq(version))
                .fetch();
    }
}
