package com.bos.resource.app.resourceowner.repository;

import com.bos.resource.app.resourceowner.model.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}
