package com.bos.resource.app.resourceowner.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "TB_USER")
public class ResourceOwner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;

    @Column(name = "ID", length = 20, nullable = false, unique = true)
    private String resourceOwnerId;
}
