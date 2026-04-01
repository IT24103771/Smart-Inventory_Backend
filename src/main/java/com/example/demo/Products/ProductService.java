package com.example.demo.Products;

import com.example.demo.Inventory.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repo;

    @Autowired
    private InventoryRepository inventoryRepository;

    /**
     * Create a new product from validated DTO.
     */
    public Product save(CreateProductRequest req) {
        validateProductRequest(req);

        Product p = new Product();
        mapDtoToEntity(req, p);
        return repo.save(p);
    }

    /**
     * Overload kept for internal use (e.g. saving existing entity).
     */
    public Product saveEntity(Product p) {
        return repo.save(p);
    }

    public List<Product> getAll() {
        return repo.findAll();
    }

    public Product getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Integer getAvailableQuantity(Long productId) {
        return inventoryRepository.getTotalQuantityByProductId(productId);
    }

    /**
     * Update an existing product from validated DTO.
     */
    public Product update(Long id, CreateProductRequest req) {
        Product existing = getById(id);
        validateProductRequest(req);
        mapDtoToEntity(req, existing);
        return repo.save(existing);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Product not found");
        }
        repo.deleteById(id);
    }

    /**
     * Maps DTO fields to entity, trimming all string values.
     */
    private void mapDtoToEntity(CreateProductRequest req, Product p) {
        p.setProductName(req.getProductName().trim());
        p.setMainCategory(req.getMainCategory().trim());
        p.setSubCategory(req.getSubCategory().trim());
        p.setItemType(req.getItemType().trim());
        p.setSupplier(req.getSupplier().trim());
        p.setCostPrice(req.getCostPrice());
        p.setSellingPrice(req.getSellingPrice());
        p.setImageUrl(req.getImageUrl() != null ? req.getImageUrl().trim() : null);
        p.setReorderLevel(req.getReorderLevel());
    }

    /**
     * Service-level validation as defense-in-depth (in addition to DTO annotations).
     */
    private void validateProductRequest(CreateProductRequest req) {
        if (req.getProductName() == null || req.getProductName().trim().isEmpty()) {
            throw new RuntimeException("Product name is required");
        }
        if (req.getProductName().trim().length() < 3) {
            throw new RuntimeException("Product name must be at least 3 characters");
        }
        if (req.getMainCategory() == null || req.getMainCategory().trim().isEmpty()) {
            throw new RuntimeException("Main category is required");
        }
        if (req.getSubCategory() == null || req.getSubCategory().trim().isEmpty()) {
            throw new RuntimeException("Sub category is required");
        }
        if (req.getItemType() == null || req.getItemType().trim().isEmpty()) {
            throw new RuntimeException("Item type is required");
        }
        if (req.getSupplier() == null || req.getSupplier().trim().isEmpty()) {
            throw new RuntimeException("Supplier is required");
        }
        if (req.getCostPrice() < 0) {
            throw new RuntimeException("Cost price must be 0 or more");
        }
        if (req.getSellingPrice() <= 0) {
            throw new RuntimeException("Selling price must be greater than 0");
        }
        if (req.getCostPrice() > req.getSellingPrice()) {
            throw new RuntimeException("Cost price cannot be greater than selling price");
        }
        if (req.getReorderLevel() < 0) {
            throw new RuntimeException("Reorder level must be 0 or more");
        }
    }
}