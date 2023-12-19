package com.bos.resource.app.fota.repository;


import com.bos.resource.app.fota.model.entity.Campaign;
import com.bos.resource.app.fota.model.entity.QCampaign;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class QCampaignRepositoryImpl implements QCampaignRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Campaign findFirstByNameStartsWithAndCompanyIdOrderByNameDesc(String newDeploymentPrefix, Long companyId) {
        QCampaign qCampaign = QCampaign.campaign;
        return queryFactory.selectFrom(qCampaign)
                .where(qCampaign.name.startsWith(newDeploymentPrefix), qCampaign.companyId.eq(companyId))
                .orderBy(qCampaign.name.desc())
                .fetchFirst();
    }
}
