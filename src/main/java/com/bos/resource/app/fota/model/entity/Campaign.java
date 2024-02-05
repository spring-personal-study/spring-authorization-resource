package com.bos.resource.app.fota.model.entity;

import com.bos.resource.app.common.domain.entity.BaseEntity;
import com.bos.resource.app.common.domain.enums.UseType;
import com.bos.resource.app.fota.model.constants.enums.CampaignStatus;
import com.bos.resource.app.fota.model.dto.ConvertedDateString;
import com.bos.resource.app.resourceowner.model.dto.ResourceOwnerDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLRestriction;

import static com.bos.resource.app.common.domain.enums.UseType.Y;
import static com.bos.resource.app.fota.model.constants.enums.CampaignStatus.ACTIVE;

@Getter
@Entity
@Table(name = "TB_FOTA_CAMPAIGN")
@DynamicUpdate
@SQLRestriction("USE_YN = 'Y'")
@NoArgsConstructor
@ToString
public class Campaign extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CAMPAIGN_ID")
    private Long id;

    @Column(name = "CAMPAIGN_NM")
    private String name;

    @Column(name = "CAMPAIGN_DESC")
    private String description;

    @Column(name = "PLATFORM_ID")
    private Integer platformId;

    @Column(name = "START_DT")
    private String startDate;

    @Column(name = "START_TM")
    private String startTime;

    @Column(name = "END_DT")
    private String endDate;

    @Column(name = "END_TM")
    private String endTime;

    @Column(name = "P2P_MODE_YN")
    @Enumerated(value = EnumType.STRING)
    private UseType p2pModeYn;

    @Column(name = "ASK_USER_YN")
    @Enumerated(value = EnumType.STRING)
    private UseType askUserYn;

    @Column(name = "LTE_USE_YN")
    @Enumerated(value = EnumType.STRING)
    private UseType lteUseYn;

    @Column(name = "STATUS")
    @Enumerated(value = EnumType.STRING)
    private CampaignStatus status;

    @Column(name = "USE_YN")
    @Enumerated(value = EnumType.STRING)
    private UseType useYn;

    @Column(name = "COMPANY_ID")
    private Long companyId;

    @Builder
    public Campaign(Long id, String name, Integer platformId, UseType p2pModeYn,
                    String startDate, String startTime, String endDate, String endTime,
                    UseType askUserYn, UseType lteUseYn, Long companyId, String username, String email, String companyName) {
        this.id = id;
        this.name = name;
        this.description = name + " campaign created by " + companyName + " company's " + username + " for new deployment.";
        this.platformId = platformId;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.p2pModeYn = p2pModeYn;
        this.askUserYn = askUserYn;
        this.lteUseYn = lteUseYn;
        this.companyId = companyId;
        super.createId = email;
        super.updateId = email;
        this.status = ACTIVE;
        this.useYn = Y;
    }

    public static Campaign createCampaign(String newCampaignName, ResourceOwnerDto requestUser, String companyName, Integer platformId, ConvertedDateString convertedDateString, UseType allowUserPostpone) {
        return Campaign.builder()
                .name(newCampaignName)
                .startDate(convertedDateString.getStartDateString())
                .endDate(convertedDateString.getEndDateString())
                .startTime(convertedDateString.getStartTimeString())
                .endTime(convertedDateString.getEndTimeString())
                .companyId(requestUser.getCompanyId())
                .platformId(platformId)
                .p2pModeYn(UseType.N)
                .askUserYn(allowUserPostpone)
                .lteUseYn(UseType.Y)
                .username(requestUser.getResourceOwnerId())
                .email(requestUser.getEmail())
                .companyName(companyName)
                .build();

    }
}
