package com.bos.resource.app.resourceowner.model.entity;

import com.bos.resource.app.common.domain.entity.BaseEntity;
import com.bos.resource.app.common.domain.enums.UseType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Entity
@SQLRestriction("USE_YN = 'Y'")
@Table(name = "TB_USER_GROUP")
@NoArgsConstructor
public class UserGroup extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_GRP_ID")
    @Comment("TB_USER_GROUP primary key")
    private Long id;

    @Setter
    @Column(name = "USER_GRP_NM")
    @Comment("user group name")
    private String userGroupNm;

    @Setter
    @Column(name = "USER_GRP_DESC")
    @Comment("user group description")
    private String userGroupDesc;

    @Setter
    @Column(name = "ADMIN_YN", nullable = false)
    @Enumerated(EnumType.STRING)
    private UseType adminYn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    private Company company;

    @Column(name = "USE_YN", nullable = false)
    @Enumerated(EnumType.STRING)
    private UseType useYn;
}