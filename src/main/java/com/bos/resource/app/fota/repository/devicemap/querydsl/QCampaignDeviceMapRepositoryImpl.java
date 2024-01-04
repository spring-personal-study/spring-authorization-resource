package com.bos.resource.app.fota.repository.devicemap.querydsl;

import com.bos.resource.app.device.model.entity.QDevice;
import com.bos.resource.app.fota.model.entity.Campaign;
import com.bos.resource.app.fota.model.entity.CampaignDeviceMap;
import com.bos.resource.app.fota.model.entity.QCampaign;
import com.bos.resource.app.fota.model.entity.QCampaignDeviceMap;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

@RequiredArgsConstructor
public class QCampaignDeviceMapRepositoryImpl implements QCampaignDeviceMapRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<CampaignDeviceMap> findByCampaignDevices(Campaign targetCampaign, Pageable pageable) {
        QCampaignDeviceMap qCampaignDeviceMap = QCampaignDeviceMap.campaignDeviceMap;
        List<CampaignDeviceMap> devices = queryFactory.select(qCampaignDeviceMap)
                .from(qCampaignDeviceMap)
                .where(qCampaignDeviceMap.campaign.eq(targetCampaign))
                .orderBy(qCampaignDeviceMap.updateDate.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();
        JPAQuery<Long> countQuery = queryFactory.select(qCampaignDeviceMap.count())
                .from(qCampaignDeviceMap)
                .where(qCampaignDeviceMap.campaign.eq(targetCampaign));
        return PageableExecutionUtils.getPage(devices, pageable, countQuery::fetchOne);
    }

    @Override
    public List<CampaignDeviceMap> findByCampaign(Campaign campaign) {
        QCampaignDeviceMap qCampaignDeviceMap = QCampaignDeviceMap.campaignDeviceMap;
        QCampaign qCampaign = QCampaign.campaign;
        QDevice qDevice = QDevice.device;

        return queryFactory.select(qCampaignDeviceMap)
                .from(qCampaignDeviceMap)
                .innerJoin(qCampaign).on(qCampaign.id.eq(qCampaignDeviceMap.campaign.id))
                .innerJoin(qDevice).on(qDevice.id.eq(qCampaignDeviceMap.device.id))
                .fetchJoin()
                .where(qCampaignDeviceMap.campaign.id.eq(campaign.getId()))
                .fetch();
    }
}
