package com.franchise.repository;

import com.franchise.model.BranchOffice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchOfficeRepository extends JpaRepository<BranchOffice, Long> {

    List<BranchOffice> findByFranchiseId(Long franchiseId);
}