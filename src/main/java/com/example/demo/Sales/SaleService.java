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

    // ✅ CREATE
    @Transactional
    public SaleResponse create(CreateSaleRequest req) {

        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + req.getProductId()));

        Inventory batch = inventoryRepository.findById(req.getBatchId())
                .orElseThrow(() -> new RuntimeException("Batch not found: " + req.getBatchId()));

        // Ensure batch belongs to the selected product
        if (!batch.getProduct().getId().equals(product.getId())) {
            throw new RuntimeException("Selected batch does not belong to selected product.");
        }

        // Optional: block expired batch sales
        LocalDate today = LocalDate.now();
        if (batch.getExpiryDate() != null && batch.getExpiryDate().isBefore(today)) {
            throw new RuntimeException("Cannot sell from an expired batch.");
        }

        int qty = req.getQuantity();
        if (batch.getQuantity() < qty) {
            throw new RuntimeException("Not enough stock in selected batch. Available: "
                    + batch.getQuantity() + ", Requested: " + qty);
        }

        // Deduct stock
        batch.setQuantity(batch.getQuantity() - qty);
        inventoryRepository.save(batch);

        // Save sale record
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


    // ✅ UPDATE (restore old batch, deduct new batch)
    @Transactional
    public SaleResponse update(Long id, CreateSaleRequest req) {

        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found: " + id));

        Product newProduct = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + req.getProductId()));

        Inventory newBatch = inventoryRepository.findById(req.getBatchId())
                .orElseThrow(() -> new RuntimeException("Batch not found: " + req.getBatchId()));

        if (!newBatch.getProduct().getId().equals(newProduct.getId())) {
            throw new RuntimeException("Selected batch does not belong to selected product.");
        }

        // restore previous stock
        Inventory oldBatch = sale.getInventoryBatch();
        oldBatch.setQuantity(oldBatch.getQuantity() + sale.getQuantity());
        inventoryRepository.save(oldBatch);

        // deduct from new batch
        int newQty = req.getQuantity();
        if (newBatch.getQuantity() < newQty) {
            throw new RuntimeException("Not enough stock in selected batch. Available: "
                    + newBatch.getQuantity() + ", Requested: " + newQty);
        }

        newBatch.setQuantity(newBatch.getQuantity() - newQty);
        inventoryRepository.save(newBatch);

        // update sale data
        sale.setProduct(newProduct);
        sale.setInventoryBatch(newBatch);
        sale.setQuantity(newQty);
        sale.setSaleDate(req.getSaleDate());

        return toResponse(saleRepository.save(sale));
    }

    // ✅ DELETE (restore stock back to batch)
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
                s.getProduct().getId(),
                s.getProduct().getName(),
                s.getInventoryBatch().getId(),
                s.getInventoryBatch().getBatchNumber(),
                s.getInventoryBatch().getExpiryDate(),
                s.getQuantity(),
                s.getSaleDate(),
                s.getCreatedAt()
        );
    }

}
