package com.example.demo.Discount;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DiscountRepository extends JpaRepository<Discount, Long> {

    @Query("SELECT d FROM Discount d JOIN FETCH d.product p ORDER BY d.createdAt DESC")
    List<Discount> findAllWithProductOrderByCreatedAtDesc();

    @Query("SELECT d FROM Discount d JOIN FETCH d.product p WHERE d.active = true ORDER BY d.createdAt DESC")
    List<Discount> findActiveWithProduct();
}
