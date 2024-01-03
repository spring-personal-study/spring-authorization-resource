package com.bos.resource.app.device.repository.device.querydsl;


import com.bos.resource.app.device.model.dto.DeviceResponseDto;
import com.bos.resource.app.device.model.entity.QDevice;
import com.bos.resource.app.device.model.entity.QDeviceDetail;
import com.bos.resource.app.fota.model.dto.CampaignRequestDto.FOTAReadyDevice;
import com.bos.resource.app.fota.model.dto.CampaignResponseDto.FotaReadyDevice.FOTAReadyDeviceContent;
import com.bos.resource.app.fota.model.entity.QCampaign;
import com.bos.resource.app.fota.model.entity.QCampaignDeviceMap;
import com.bos.resource.app.resourceowner.model.entity.QCompany;
import com.bos.resource.app.resourceowner.model.entity.QResourceOwner;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.querydsl.core.types.dsl.Expressions.asNumber;

@RequiredArgsConstructor
public class QDeviceRepositoryImpl implements QDeviceRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<DeviceResponseDto.DeviceDto> findAssetByEnrolledUser(Long userId) {
        QDevice qDevice = QDevice.device;
        QDeviceDetail qDeviceDetail = QDeviceDetail.deviceDetail;
        QResourceOwner qResourceOwner = QResourceOwner.resourceOwner;

        return queryFactory
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

    @Override
    public Page<FOTAReadyDeviceContent> findFOTAReadyDevice(Long companyId, FOTAReadyDevice campaignDevice) {
        String fotaReady = campaignDevice.fotaReady();
        String detailLevel = campaignDevice.detailLevel();
        PageRequest pageable = PageRequest.of(campaignDevice.offset(), campaignDevice.size());
        if (fotaReady.equals("1") && detailLevel.equals("1")) {
            return getAllDevicesDetailRegardlessFOTAReadyOrNot(companyId, pageable);
        }
        if (fotaReady.equals("1") && detailLevel.equals("0")) {
            return getAllDevicesRegardlessFOTAReadyOrNot(companyId, pageable);
        }
        if (fotaReady.equals("0") && detailLevel.equals("0")) {
            return getDevicesFOTAReadyOnly(companyId, pageable);
        }
        // fotaReady.equals("0") && detailLevel.equals("1")
        return getFOTAReadyOnlyWithDetail(companyId, pageable);
    }

    private Page<FOTAReadyDeviceContent> getAllDevicesDetailRegardlessFOTAReadyOrNot(Long companyId, PageRequest pageable) {
        QDevice qDevice = QDevice.device;
        QDeviceDetail qDeviceDetail = QDeviceDetail.deviceDetail;
        QResourceOwner qResourceOwner = QResourceOwner.resourceOwner;
        QCompany qCompany = QCompany.company;
        QCampaignDeviceMap qCampaignDeviceMap = QCampaignDeviceMap.campaignDeviceMap;

        List<FOTAReadyDeviceContent> fotaReadyWithDetail = queryFactory
                .select(Projections.constructor(
                        FOTAReadyDeviceContent.class,
                        qDevice.serialNumber,
                        qDeviceDetail.modelName,
                        new CaseBuilder()
                                .when(qCampaignDeviceMap.campaign.id.isNull())
                                .then(0)
                                .otherwise(1).as("fotaReady"),
                        qDeviceDetail.updatedDate,
                        qDeviceDetail.osBuildNumber,
                        qDeviceDetail.osVersion)).distinct()
                .from(qDevice)
                .rightJoin(qDeviceDetail).on(qDevice.eq(qDeviceDetail.device))
                .leftJoin(qCampaignDeviceMap).on(qDevice.eq(qCampaignDeviceMap.device))
                .innerJoin(qResourceOwner).on(qResourceOwner.eq(qDevice.resourceOwner))
                .innerJoin(qCompany).on(qCompany.id.eq(qResourceOwner.companyId))
                .fetchJoin()
                .where(qCompany.id.eq(companyId))
                .orderBy(qDeviceDetail.updatedDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(qDevice.countDistinct())
                .from(qDevice)
                .rightJoin(qDeviceDetail).on(qDevice.eq(qDeviceDetail.device))
                .leftJoin(qCampaignDeviceMap).on(qDevice.eq(qCampaignDeviceMap.device))
                .innerJoin(qResourceOwner).on(qResourceOwner.eq(qDevice.resourceOwner))
                .innerJoin(qCompany).on(qCompany.id.eq(qResourceOwner.companyId))
                .fetchJoin()
                .where(qCompany.id.eq(companyId));

        return PageableExecutionUtils.getPage(fotaReadyWithDetail, pageable, countQuery::fetchOne);
    }

    private Page<FOTAReadyDeviceContent> getFOTAReadyOnlyWithDetail(Long companyId, PageRequest pageable) {
        QDevice qDevice = QDevice.device;
        QDeviceDetail qDeviceDetail = QDeviceDetail.deviceDetail;
        QCampaignDeviceMap qCampaignDeviceMap = QCampaignDeviceMap.campaignDeviceMap;
        QCampaign qCampaign = QCampaign.campaign;

        List<FOTAReadyDeviceContent> fotaReadyWithDetail = queryFactory
                .select(Projections.constructor(
                        FOTAReadyDeviceContent.class,
                        qDevice.serialNumber,
                        qDeviceDetail.modelName,
                        asNumber(qCampaignDeviceMap.campaign.id).as("fotaReady"),
                        qDeviceDetail.updatedDate,
                        qDeviceDetail.osBuildNumber,
                        qDeviceDetail.osVersion)).distinct()
                .from(qDevice)
                .innerJoin(qDeviceDetail).on(qDeviceDetail.device.eq(qDevice))
                .innerJoin(qCampaignDeviceMap).on(qDevice.eq(qCampaignDeviceMap.device))
                .innerJoin(qCampaign).on(qCampaignDeviceMap.campaign.eq(qCampaign))
                .fetchJoin()
                .where(qCampaign.companyId.eq(companyId))
                .orderBy(qDeviceDetail.updatedDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(qDevice.countDistinct())
                .from(qDevice)
                .innerJoin(qDeviceDetail).on(qDeviceDetail.device.eq(qDevice))
                .innerJoin(qCampaignDeviceMap).on(qDevice.eq(qCampaignDeviceMap.device))
                .innerJoin(qCampaign).on(qCampaignDeviceMap.campaign.eq(qCampaign))
                .fetchJoin()
                .where(qCampaign.companyId.eq(companyId));

        return PageableExecutionUtils.getPage(fotaReadyWithDetail, pageable, countQuery::fetchOne);
    }

    private Page<FOTAReadyDeviceContent> getAllDevicesRegardlessFOTAReadyOrNot(Long companyId, PageRequest pageable) {
        QDevice qDevice = QDevice.device;
        QDeviceDetail qDeviceDetail = QDeviceDetail.deviceDetail;
        QResourceOwner qResourceOwner = QResourceOwner.resourceOwner;
        QCompany qCompany = QCompany.company;
        QCampaignDeviceMap qCampaignDeviceMap = QCampaignDeviceMap.campaignDeviceMap;

        List<FOTAReadyDeviceContent> fotaReady = queryFactory.select(
                        Projections.constructor(
                                FOTAReadyDeviceContent.class,
                                QDevice.device.serialNumber,
                                QDeviceDetail.deviceDetail.modelName
                        )).distinct()
                .from(qDevice)
                .rightJoin(qDeviceDetail).on(qDeviceDetail.device.eq(qDevice))
                .leftJoin(qCampaignDeviceMap).on(qCampaignDeviceMap.device.eq(qDevice))
                .join(qResourceOwner).on(qDevice.resourceOwner.eq(qResourceOwner))
                .join(qCompany).on(qCompany.id.eq(qResourceOwner.companyId))
                .fetchJoin()
                .where(qCompany.id.eq(companyId))
                .orderBy(qDeviceDetail.updatedDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(qDevice.countDistinct())
                .from(qDevice)
                .rightJoin(qDeviceDetail).on(qDeviceDetail.device.eq(qDevice))
                .leftJoin(qCampaignDeviceMap).on(qCampaignDeviceMap.device.eq(qDevice))
                .join(qResourceOwner).on(qDevice.resourceOwner.eq(qResourceOwner))
                .join(qCompany).on(qCompany.id.eq(qResourceOwner.companyId))
                .fetchJoin()
                .where(qCompany.id.eq(companyId));

        return PageableExecutionUtils.getPage(fotaReady, pageable, countQuery::fetchOne);
    }

    private Page<FOTAReadyDeviceContent> getDevicesFOTAReadyOnly(Long companyId, PageRequest pageable) {
        QDevice qDevice = QDevice.device;
        QDeviceDetail qDeviceDetail = QDeviceDetail.deviceDetail;
        QCampaignDeviceMap qCampaignDeviceMap = QCampaignDeviceMap.campaignDeviceMap;
        QCampaign qCampaign = QCampaign.campaign;

        List<FOTAReadyDeviceContent> fotaReadyWithDetail = queryFactory
                .select(Projections.constructor(
                                FOTAReadyDeviceContent.class,
                                qDevice.serialNumber,
                                qDeviceDetail.modelName
                        )
                ).distinct()
                .from(qDevice)
                .innerJoin(qDeviceDetail).on(qDevice.deviceDetail.eq(qDeviceDetail))
                .innerJoin(qCampaignDeviceMap).on(qDevice.eq(qCampaignDeviceMap.device))
                .innerJoin(qCampaign).on(qCampaign.eq(qCampaignDeviceMap.campaign))
                .fetchJoin()
                .where(qCampaign.companyId.eq(companyId))
                .orderBy(qDeviceDetail.updatedDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(qDevice.countDistinct())
                .from(qDevice)
                .innerJoin(qDeviceDetail).on(qDevice.deviceDetail.eq(qDeviceDetail))
                .innerJoin(qCampaignDeviceMap).on(qDevice.eq(qCampaignDeviceMap.device))
                .innerJoin(qCampaign).on(qCampaign.eq(qCampaignDeviceMap.campaign))
                .fetchJoin()
                .where(qCampaign.companyId.eq(companyId));

        return PageableExecutionUtils.getPage(fotaReadyWithDetail, pageable, countQuery::fetchOne);
    }


}
