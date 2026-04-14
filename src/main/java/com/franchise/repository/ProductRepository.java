package com.franchise.repository;

import com.franchise.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByBranchOfficeId(Long branchOfficeId);

    // This query powers the "max stock per branch" endpoint
    @Query("""
        SELECT p FROM Product p
        WHERE p.branchOffice.id = :branchOfficeId
        AND p.stock = (
            SELECT MAX(p2.stock) FROM Product p2
            WHERE p2.branchOffice.id = :branchOfficeId
        )
    """)
    Optional<Product> findTopStockByBranchOfficeId(@Param("branchOfficeId") Long branchOfficeId);
}