package com.example.demo.Products;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repo;

    public Product save(Product p) {
        if (p.getName() == null || p.getName().trim().isEmpty()) {
            throw new RuntimeException("Product name is required");
        }
        if (p.getCategory() == null || p.getCategory().trim().isEmpty()) {
            throw new RuntimeException("Category is required");
        }
        if (p.getBrand() == null || p.getBrand().trim().isEmpty()) {
            throw new RuntimeException("Brand is required");
        }
        if (p.getPrice() <= 0) {
            throw new RuntimeException("Price must be > 0");
        }
        if (p.getReorderLevel() < 0) {
            throw new RuntimeException("Reorder level must be >= 0");
        }

        p.setName(p.getName().trim());
        p.setCategory(p.getCategory().trim());
        p.setBrand(p.getBrand().trim());

        return repo.save(p);
    }

    public List<Product> getAll() {
        return repo.findAll();
    }

    public Product getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Product update(Long id, Product newData) {
        Product existing = getById(id);

        existing.setName(newData.getName());
        existing.setCategory(newData.getCategory());
        existing.setBrand(newData.getBrand());
        existing.setPrice(newData.getPrice());
        existing.setReorderLevel(newData.getReorderLevel());

        return save(existing);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Product not found");
        }
        repo.deleteById(id);
    }
}
