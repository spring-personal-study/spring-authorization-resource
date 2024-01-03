package com.bos.resource.app.fota.repository;


import com.bos.resource.app.fota.model.dto.CampaignStatusAggregation;
import com.bos.resource.app.fota.model.entity.Campaign;
import com.bos.resource.app.fota.model.entity.QCampaign;
import com.bos.resource.app.fota.model.entity.QCampaignDeviceMap;
import com.bos.resource.app.fota.model.enums.CampaignDeviceStatus;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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
    public List<CampaignStatusAggregation> findCampaignStatusByCompanyIdAndCampaignIdAndBetweenDate(
           Long companyId, String campaignId, LocalDateTime startDate, LocalDateTime endDate
    ) {
        QCampaign qCampaign = QCampaign.campaign;
        QCampaignDeviceMap qCampaignDeviceMap = QCampaignDeviceMap.campaignDeviceMap;

        return queryFactory.select(
                        Projections.constructor(
                                CampaignStatusAggregation.class,
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
                .where(qCampaign.name.eq(campaignId), qCampaign.createDt.between(startDate, endDate), qCampaign.companyId.eq(companyId))
                .groupBy(qCampaign.status, qCampaign.updateDt)
                .fetch();
    }

    @Override
    public CampaignStatusAggregation findCampaignStatusByCampaign(Campaign targetCampaign) {
        QCampaign qCampaign = QCampaign.campaign;
        QCampaignDeviceMap qCampaignDeviceMap = QCampaignDeviceMap.campaignDeviceMap;

        return queryFactory.select(
                        Projections.constructor(
                                CampaignStatusAggregation.class,
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
        return queryFactory.select(QCampaign.campaign)
                .from(QCampaign.campaign)
                .where(QCampaign.campaign.companyId.eq(companyId).and(QCampaign.campaign.name.eq(campaignName)))
                .fetchOne();
    }
}
