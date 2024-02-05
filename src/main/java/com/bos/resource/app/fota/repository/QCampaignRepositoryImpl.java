package com.bos.resource.app.fota.repository;


import com.bos.resource.app.fota.model.constants.enums.CampaignDeviceStatus;
import com.bos.resource.app.fota.model.dto.CampaignStatusAggregation;
import com.bos.resource.app.fota.model.entity.Campaign;
import com.bos.resource.app.fota.model.entity.QCampaign;
import com.bos.resource.app.fota.model.entity.QCampaignDeviceMap;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import static com.bos.resource.app.fota.model.constants.enums.CampaignStatus.ACTIVE;
import static com.querydsl.core.types.Projections.constructor;
import static java.util.Objects.requireNonNullElseGet;
import static org.springframework.util.StringUtils.capitalize;

@RequiredArgsConstructor
public class QCampaignRepositoryImpl implements QCampaignRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Campaign findFirstByNameStartsWithAndCompanyIdOrderByNameDesc(
            String newDeploymentPrefix, Long companyId
    ) {
        QCampaign qCampaign = QCampaign.campaign;
        return queryFactory.selectFrom(qCampaign)
                .where(qCampaign.name.startsWith(newDeploymentPrefix), qCampaign.companyId.eq(companyId))
                .orderBy(qCampaign.name.desc())
                .fetchFirst();
    }

    @Override
    public CampaignStatusAggregation findCampaignStatusByCompanyIdAndCampaignIdAndBetweenDateAndStatus(
            Long companyId, String campaignId, LocalDateTime startDate, LocalDateTime endDate, String status
    ) {
        QCampaign qCampaign = QCampaign.campaign;
        QCampaignDeviceMap qCampaignDeviceMap = QCampaignDeviceMap.campaignDeviceMap;

        return queryFactory.select(
                        constructor(
                                CampaignStatusAggregation.class,
                                qCampaign.name,
                                qCampaign.status.as("deploymentStatus"),
                                qCampaignDeviceMap.device.id.count().as("totalDevices"),
                                new CaseBuilder()
                                        .when(qCampaignDeviceMap.status.eq(CampaignDeviceStatus.PENDING))
                                        .then(1).otherwise(0).sum().as("scheduled"),
                                new CaseBuilder()
                                        .when(qCampaignDeviceMap.status.eq(CampaignDeviceStatus.DOWNLOADING))
                                        .then(1).otherwise(0).sum().as("downloading"),
                                new CaseBuilder()
                                        .when(qCampaignDeviceMap.status.eq(CampaignDeviceStatus.DOWNLOADED))
                                        .then(1).otherwise(0).sum().as("awaitingInstall"),
                                new CaseBuilder()
                                        .when(qCampaignDeviceMap.status.eq(CampaignDeviceStatus.COMPLETED))
                                        .then(1).otherwise(0).sum().as("completed"),
                                new CaseBuilder()
                                        .when(qCampaignDeviceMap.status.eq(CampaignDeviceStatus.FAIL))
                                        .then(1).otherwise(0).sum().as("failed"),
                                qCampaign.updateDt
                        )
                )
                .from(qCampaign)
                .innerJoin(qCampaignDeviceMap).on(qCampaign.id.eq(qCampaignDeviceMap.campaign.id))
                .fetchJoin()
                .where(eqCampaignId(campaignId), betweenCreateDt(startDate, endDate), isActive(status), qCampaign.companyId.eq(companyId))
                .groupBy(qCampaign.status, qCampaign.updateDt)
                .fetchOne();
    }

    @Override
    public CampaignStatusAggregation findCampaignStatusByCampaign(Campaign targetCampaign) {
        QCampaign qCampaign = QCampaign.campaign;
        QCampaignDeviceMap qCampaignDeviceMap = QCampaignDeviceMap.campaignDeviceMap;

        return queryFactory.select(
                        constructor(
                                CampaignStatusAggregation.class,
                                qCampaign.name,
                                qCampaign.status.as("deploymentStatus"),
                                qCampaignDeviceMap.device.id.count().as("totalDevices"),
                                new CaseBuilder()
                                        .when(qCampaignDeviceMap.status.eq(CampaignDeviceStatus.PENDING))
                                        .then(1).otherwise(0).sum().as("scheduled"),
                                new CaseBuilder()
                                        .when(qCampaignDeviceMap.status.eq(CampaignDeviceStatus.DOWNLOADING))
                                        .then(1).otherwise(0).sum().as("downloading"),
                                new CaseBuilder()
                                        .when(qCampaignDeviceMap.status.eq(CampaignDeviceStatus.DOWNLOADED))
                                        .then(1).otherwise(0).sum().as("awaitingInstall"),
                                new CaseBuilder()
                                        .when(qCampaignDeviceMap.status.eq(CampaignDeviceStatus.COMPLETED))
                                        .then(1).otherwise(0).sum().as("completed"),
                                new CaseBuilder()
                                        .when(qCampaignDeviceMap.status.eq(CampaignDeviceStatus.FAIL))
                                        .then(1).otherwise(0).sum().as("failed"),
                                qCampaign.updateDt
                        )
                )
                .from(qCampaign)
                .innerJoin(qCampaignDeviceMap).on(qCampaign.id.eq(qCampaignDeviceMap.campaign.id))
                .fetchJoin()
                .where(qCampaign.name.eq(targetCampaign.getName()))
                .groupBy(qCampaign.status, qCampaign.updateDt)
                .fetchOne();
    }

    @Override
    public Campaign findByCampaignName(Long companyId, String campaignName) {
        QCampaign qCampaign = QCampaign.campaign;
        return queryFactory.select(qCampaign)
                .from(qCampaign)
                .where(qCampaign.companyId.eq(companyId).and(qCampaign.name.eq(campaignName)))
                .fetchOne();
    }

    private BooleanExpression eqCampaignId(String campaignId) {
        return campaignId == null ? null : QCampaign.campaign.name.eq(campaignId);
    }

    private BooleanExpression betweenCreateDt(LocalDateTime startDate, LocalDateTime endDate) {
        QCampaign qCampaign = QCampaign.campaign;
        if (startDate == null && endDate == null) {
            return qCampaign.createDt.between(LocalDateTime.now().minusDays(90), LocalDateTime.now());
        } else if (startDate == null) {
            return qCampaign.createDt.between(endDate.minusDays(90), endDate);
        }
        return qCampaign.createDt.between(startDate, requireNonNullElseGet(endDate, () -> startDate.plusDays(90)));
    }

    private BooleanExpression isActive(String status) {
        return (status == null || "ALL".equals(capitalize(status))) ? null : QCampaign.campaign.status.eq(ACTIVE);
    }
}
