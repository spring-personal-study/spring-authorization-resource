package com.bos.resource.app.fota.repository.firmware.querydsl;

import com.bos.resource.app.fota.model.dto.CampaignRequestDto.Notification;
import com.bos.resource.app.fota.model.dto.NotificationFirmwareInfoDto;
import com.bos.resource.app.fota.model.entity.Firmware;
import com.bos.resource.app.fota.model.entity.QFirmware;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class QFirmwareRepositoryImpl implements QFirmwareRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Firmware> findByModelAndVersion(String model, String version) {
        QFirmware qFirmware = QFirmware.firmware;
        return queryFactory.selectFrom(qFirmware)
                .where(qFirmware.model.eq(model), qFirmware.version.eq(version))
                .fetch();
    }

    @Override
    public Firmware findOneLatestByModel(String model) {
        return queryFactory.selectFrom(QFirmware.firmware)
                .where(QFirmware.firmware.model.eq(model))
                .orderBy(QFirmware.firmware.version.desc())
                .fetchFirst();
    }

    @Override
    public Page<NotificationFirmwareInfoDto> findUpdatableFirmwareByCompanyIdAndBetweenRegisteredDate(Set<String> modelNames, Long companyId, Notification notification, Pageable pageable) {
        QFirmware qFirmware = QFirmware.firmware;

        List<NotificationFirmwareInfoDto> updatableVersions = queryFactory
                .select(Projections.constructor(
                        NotificationFirmwareInfoDto.class,
                        qFirmware.model,
                        qFirmware.version,
                        qFirmware.packageType,
                        qFirmware.createDt
                ))
                .from(qFirmware)
                .where(qFirmware.model.in(modelNames),
                        betweenRegisteredDate(notification.params().fromTime(), notification.params().toTime()),
                        // qFirmware.companyId.eq(1L) : the firmwares uploaded by bluebird company.
                        qFirmware.companyId.eq(companyId).or(qFirmware.companyId.eq(1L))
                )
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();


        JPAQuery<Long> countQuery = queryFactory
                .select(qFirmware.version.count())
                .from(qFirmware)
                .where(qFirmware.model.in(modelNames),
                        betweenRegisteredDate(notification.params().fromTime(), notification.params().toTime()),
                        // qFirmware.companyId.eq(1L) : the firmwares uploaded by bluebird company.
                        qFirmware.companyId.eq(companyId).or(qFirmware.companyId.eq(1L))
                );

        return PageableExecutionUtils.getPage(updatableVersions, pageable, countQuery::fetchOne);
    }

    private BooleanExpression betweenRegisteredDate(LocalDateTime fromTime, LocalDateTime toTime) {
        if (fromTime == null && toTime == null) return null;
        QFirmware qFirmware = QFirmware.firmware;
        if (fromTime != null && toTime != null) {
            return qFirmware.createDt.between(fromTime, toTime);
        } else if (fromTime != null) {
            return qFirmware.createDt.after(fromTime);
        } else {
            return qFirmware.createDt.before(toTime);
        }

    }
}
