package com.example.demo.Sales;

import com.example.demo.Products.Product;
import com.example.demo.Products.ProductRepository;
import com.example.demo.Inventory.InventoryService; // ✅ ADD THIS

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final InventoryService inventoryService; // ✅ ADD THIS

    // ✅ UPDATED CONSTRUCTOR
    public SaleService(SaleRepository saleRepository,
                       ProductRepository productRepository,
                       InventoryService inventoryService) {
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
        this.inventoryService = inventoryService;
    }

    // ✅ CREATE SALE + REDUCE INVENTORY
    @Transactional
    public SaleResponse create(CreateSaleRequest req) {

        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + req.getProductId()));

        // ✅ REDUCE INVENTORY FIRST
        inventoryService.consumeStock(product.getId(), req.getQuantity());

        Sale sale = new Sale();
        sale.setProduct(product);
        sale.setQuantity(req.getQuantity());
        sale.setSaleDate(req.getSaleDate());

        Sale saved = saleRepository.save(sale);

        return toResponse(saved);
    }

    // GET ALL SALES
    public List<SaleResponse> getAll() {
        return saleRepository.findAllWithProductOrderByDateDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // UPDATE SALE (optional: advanced logic to restore previous stock)
    @Transactional
    public SaleResponse update(Long id, CreateSaleRequest req) {

        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found: " + id));

        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + req.getProductId()));

        // Optional improvement: restore previous quantity first
        // inventoryService.restoreStock(...)

        // Reduce new quantity
        inventoryService.consumeStock(product.getId(), req.getQuantity());

        sale.setProduct(product);
        sale.setQuantity(req.getQuantity());
        sale.setSaleDate(req.getSaleDate());

        Sale saved = saleRepository.save(sale);

        return toResponse(saved);
    }

    // DELETE SALE
    public void delete(Long id) {
        saleRepository.deleteById(id);
    }

    // RESPONSE MAPPER
    private SaleResponse toResponse(Sale s) {
        return new SaleResponse(
                s.getId(),
                s.getProduct().getId(),
                s.getProduct().getName(),
                s.getQuantity(),
                s.getSaleDate(),
                s.getCreatedAt()
        );
    }
}
