package com.example.demo.Inventory;

import com.example.demo.Products.Product;
import com.example.demo.Products.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    public InventoryService(InventoryRepository inventoryRepository,
                            ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
    }

    public InventoryResponse create(CreateInventoryRequest req) {
        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + req.getProductId()));

        Inventory inv = new Inventory();
        inv.setProduct(product);
        inv.setBatchNumber(req.getBatchNumber().trim());
        inv.setQuantity(req.getQuantity());
        inv.setExpiryDate(req.getExpiryDate());

        Inventory saved = inventoryRepository.save(inv);
        return toResponse(saved);
    }

    public List<InventoryResponse> getAll() {
        return inventoryRepository.findAllWithProductOrderByExpiryAsc()
                .stream().map(this::toResponse).toList();
    }

    public InventoryResponse update(Long id, CreateInventoryRequest req) {
        Inventory inv = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found: " + id));

        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + req.getProductId()));

        inv.setProduct(product);
        inv.setBatchNumber(req.getBatchNumber().trim());
        inv.setQuantity(req.getQuantity());
        inv.setExpiryDate(req.getExpiryDate());

        Inventory saved = inventoryRepository.save(inv);
        return toResponse(saved);
    }

    public void delete(Long id) {
        inventoryRepository.deleteById(id);
    }

    /* =========================================================
       ✅ NEW: Consume stock for Sales (FEFO by expiry date)
       - Uses earliest expiry first
       - Ignores expired batches
       - Throws error if not enough stock
       ========================================================= */
    @Transactional
    public void consumeStock(Long productId, int saleQty) {
        if (saleQty <= 0) throw new RuntimeException("Sale quantity must be greater than 0.");

        // get batches for this product sorted by expiry
        List<Inventory> batches = inventoryRepository.findByProductIdOrderByExpiryAsc(productId);

        LocalDate today = LocalDate.now();

        // only use non-expired batches
        List<Inventory> usable = batches.stream()
                .filter(b -> b.getQuantity() != null && b.getQuantity() > 0)
                .filter(b -> b.getExpiryDate() != null && !b.getExpiryDate().isBefore(today))
                .toList();

        int totalAvailable = usable.stream().mapToInt(Inventory::getQuantity).sum();

        if (totalAvailable < saleQty) {
            throw new RuntimeException("Not enough stock. Available: " + totalAvailable + ", Requested: " + saleQty);
        }

        int remaining = saleQty;

        for (Inventory b : usable) {
            if (remaining == 0) break;

            int available = b.getQuantity();
            int used = Math.min(available, remaining);

            b.setQuantity(available - used);
            remaining -= used;

            inventoryRepository.save(b);
        }
    }

    /* ========================================================= */

    private String status(LocalDate expiry) {
        LocalDate today = LocalDate.now();
        if (expiry.isBefore(today)) return "Expired";
        if (!expiry.isAfter(today.plusDays(7))) return "Expiring Soon";
        return "Safe";
    }

    private InventoryResponse toResponse(Inventory inv) {
        return new InventoryResponse(
                inv.getId(),
                inv.getProduct().getId(),
                inv.getProduct().getName(), // change if your field is not "name"
                inv.getBatchNumber(),
                inv.getQuantity(),
                inv.getExpiryDate(),
                status(inv.getExpiryDate()),
                inv.getCreatedAt()
        );
    }
}
