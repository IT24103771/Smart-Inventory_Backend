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

    // ✅ CREATE (blocks duplicate batch per product)
    public InventoryResponse create(CreateInventoryRequest req) {

        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + req.getProductId()));

        String batchNo = req.getBatchNumber() == null ? "" : req.getBatchNumber().trim();
        if (batchNo.isEmpty()) throw new RuntimeException("Batch number is required.");

        // ✅ Prevent Milk + B01 duplication
        if (inventoryRepository.existsByProductIdAndBatchNumber(req.getProductId(), batchNo)) {
            throw new RuntimeException("This batch number already exists for the selected product.");
        }

        Inventory inv = new Inventory();
        inv.setProduct(product);
        inv.setBatchNumber(batchNo);
        inv.setQuantity(req.getQuantity());
        inv.setExpiryDate(req.getExpiryDate());

        Inventory saved = inventoryRepository.save(inv);
        return toResponse(saved);
    }

    public List<InventoryResponse> getAll() {
        return inventoryRepository.findAllWithProductOrderByExpiryAsc()
                .stream().map(this::toResponse).toList();
    }

    // ✅ NEW: For Sales page dropdown (only qty > 0, earliest expiry first)
    public List<InventoryResponse> getAvailableBatches(Long productId) {
        return inventoryRepository.findAvailableBatchesByProductId(productId)
                .stream().map(this::toResponse).toList();
    }

    // ✅ UPDATE (blocks duplicates if user tries to change into an existing batch)
    public InventoryResponse update(Long id, CreateInventoryRequest req) {

        Inventory inv = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found: " + id));

        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + req.getProductId()));

        String newBatchNo = req.getBatchNumber() == null ? "" : req.getBatchNumber().trim();
        if (newBatchNo.isEmpty()) throw new RuntimeException("Batch number is required.");

        Long newProductId = product.getId();

        // ✅ If user is changing product or batch number, check duplicates
        boolean changedProduct = !inv.getProduct().getId().equals(newProductId);
        boolean changedBatch = !inv.getBatchNumber().equalsIgnoreCase(newBatchNo);

        if (changedProduct || changedBatch) {
            // If another record already has same productId + batchNo => block
            if (inventoryRepository.existsByProductIdAndBatchNumber(newProductId, newBatchNo)) {
                throw new RuntimeException("This batch number already exists for the selected product.");
            }
        }

        inv.setProduct(product);
        inv.setBatchNumber(newBatchNo);
        inv.setQuantity(req.getQuantity());
        inv.setExpiryDate(req.getExpiryDate());

        Inventory saved = inventoryRepository.save(inv);
        return toResponse(saved);
    }

    // ✅ DELETE (safer)
    public void delete(Long id) {
        if (!inventoryRepository.existsById(id)) {
            throw new RuntimeException("Inventory not found: " + id);
        }
        inventoryRepository.deleteById(id);
    }

    /* =========================================================
       ✅ Existing: Consume stock for Sales (FEFO auto by expiry date)
       - Uses earliest expiry first
       - Ignores expired batches
       - Throws error if not enough stock
       ========================================================= */
    @Transactional
    public void consumeStock(Long productId, int saleQty) {
        if (saleQty <= 0) throw new RuntimeException("Sale quantity must be greater than 0.");

        List<Inventory> batches = inventoryRepository.findByProductIdOrderByExpiryAsc(productId);

        LocalDate today = LocalDate.now();

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
