package com.example.demo.Sales;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    @Query("""
            SELECT s FROM Sale s
            JOIN FETCH s.product
            JOIN FETCH s.inventoryBatch
            ORDER BY s.saleDate DESC, s.id DESC
           """)
    List<Sale> findAllWithProductOrderByDateDesc();

    @Query("SELECT COALESCE(SUM(s.quantity),0) FROM Sale s WHERE s.saleDate = :date")
    Long getTotalSoldByDate(@Param("date") java.time.LocalDate date);

}
