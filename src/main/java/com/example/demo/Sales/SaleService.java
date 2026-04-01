package com.example.demo.Sales;

import com.example.demo.Inventory.Inventory;
import com.example.demo.Inventory.InventoryRepository;
import com.example.demo.Products.Product;
import com.example.demo.Products.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public SaleService(SaleRepository saleRepository,
                       ProductRepository productRepository,
                       InventoryRepository inventoryRepository) {
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public SaleResponse create(CreateSaleRequest req) {

        if (req.getQuantity() == null || req.getQuantity() < 1) {
            throw new RuntimeException("Sale quantity must be at least 1.");
        }

        if (req.getSaleDate() == null) {
            throw new RuntimeException("Sale date is required.");
        }

        if (req.getSaleDate().isAfter(LocalDate.now())) {
            throw new RuntimeException("Sale date cannot be in the future.");
        }

        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + req.getProductId()));

        Inventory batch = inventoryRepository.findById(req.getBatchId())
                .orElseThrow(() -> new RuntimeException("Batch not found: " + req.getBatchId()));

        if (!batch.getProduct().getProductId().equals(product.getProductId())) {
            throw new RuntimeException("Selected batch does not belong to selected product.");
        }

        LocalDate today = LocalDate.now();
        if (batch.getExpiryDate() != null && batch.getExpiryDate().isBefore(today)) {
            throw new RuntimeException("Cannot sell from an expired batch.");
        }

        int qty = req.getQuantity();
        if (batch.getQuantity() < qty) {
            throw new RuntimeException("Not enough stock in selected batch. Available: "
                    + batch.getQuantity() + ", Requested: " + qty);
        }

        batch.setQuantity(batch.getQuantity() - qty);
        inventoryRepository.save(batch);

        Sale sale = new Sale();
        sale.setProduct(product);
        sale.setInventoryBatch(batch);
        sale.setQuantity(qty);
        sale.setSaleDate(req.getSaleDate());

        Sale saved = saleRepository.save(sale);
        return toResponse(saved);
    }

    public List<SaleResponse> getAll() {
        return saleRepository.findAllWithProductOrderByDateDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public SaleResponse update(Long id, CreateSaleRequest req) {

        if (req.getQuantity() == null || req.getQuantity() < 1) {
            throw new RuntimeException("Sale quantity must be at least 1.");
        }

        if (req.getSaleDate() == null) {
            throw new RuntimeException("Sale date is required.");
        }

        if (req.getSaleDate().isAfter(LocalDate.now())) {
            throw new RuntimeException("Sale date cannot be in the future.");
        }

        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found: " + id));

        Product newProduct = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + req.getProductId()));

        Inventory newBatch = inventoryRepository.findById(req.getBatchId())
                .orElseThrow(() -> new RuntimeException("Batch not found: " + req.getBatchId()));

        if (!newBatch.getProduct().getProductId().equals(newProduct.getProductId())) {
            throw new RuntimeException("Selected batch does not belong to selected product.");
        }

        Inventory oldBatch = sale.getInventoryBatch();
        oldBatch.setQuantity(oldBatch.getQuantity() + sale.getQuantity());
        inventoryRepository.save(oldBatch);

        int newQty = req.getQuantity();
        if (newBatch.getQuantity() < newQty) {
            throw new RuntimeException("Not enough stock in selected batch. Available: "
                    + newBatch.getQuantity() + ", Requested: " + newQty);
        }

        newBatch.setQuantity(newBatch.getQuantity() - newQty);
        inventoryRepository.save(newBatch);

        sale.setProduct(newProduct);
        sale.setInventoryBatch(newBatch);
        sale.setQuantity(newQty);
        sale.setSaleDate(req.getSaleDate());

        return toResponse(saleRepository.save(sale));
    }

    @Transactional
    public void delete(Long id) {

        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found: " + id));

        Inventory batch = sale.getInventoryBatch();
        batch.setQuantity(batch.getQuantity() + sale.getQuantity());
        inventoryRepository.save(batch);

        saleRepository.deleteById(id);
    }

    private SaleResponse toResponse(Sale s) {
        return new SaleResponse(
                s.getId(),
                s.getProduct().getProductId(),
                s.getProduct().getProductName(),
                s.getInventoryBatch().getId(),
                s.getInventoryBatch().getBatchNumber(),
                s.getInventoryBatch().getExpiryDate(),
                s.getQuantity(),
                s.getSaleDate(),
                s.getCreatedAt()
        );
    }
}