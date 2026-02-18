package com.example.demo.Inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    // ✅ For inventory page listing (all batches with product info)
    @Query("SELECT i FROM Inventory i JOIN FETCH i.product p ORDER BY i.expiryDate ASC")
    List<Inventory> findAllWithProductOrderByExpiryAsc();

    // ✅ IMPORTANT: for Sales -> reduce stock (only this product, earliest expiry first)
    @Query("SELECT i FROM Inventory i JOIN FETCH i.product p " +
            "WHERE p.id = :productId ORDER BY i.expiryDate ASC")
    List<Inventory> findByProductIdOrderByExpiryAsc(@Param("productId") Long productId);

    // ✅ Optional: total available quantity for a product (helps validation)
    @Query("SELECT COALESCE(SUM(i.quantity),0) FROM Inventory i WHERE i.product.id = :productId")
    Integer getTotalQuantityByProductId(@Param("productId") Long productId);
}
