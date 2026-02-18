package com.example.demo.Sales;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    @Query("SELECT s FROM Sale s JOIN FETCH s.product p ORDER BY s.saleDate DESC, s.id DESC")
    List<Sale> findAllWithProductOrderByDateDesc();
}
