package com.bos.resource.app.fota.repository;


import com.bos.resource.app.fota.model.entity.Campaign;
import com.bos.resource.app.fota.model.entity.QFOTACampaign;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class QCampaignRepositoryImpl implements QCampaignRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Campaign findFirstByNameStartsWithAndCompanyIdOrderByNameDesc(String newDeploymentPrefix, Long companyId) {
        QFOTACampaign qfotaCampaign = QFOTACampaign.fOTACampaign;
        return queryFactory.selectFrom(qfotaCampaign)
                .where(qfotaCampaign.name.startsWith(newDeploymentPrefix), qfotaCampaign.companyId.eq(companyId))
                .orderBy(qfotaCampaign.name.desc())
                .fetchFirst();
    }
}
