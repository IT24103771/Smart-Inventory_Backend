package com.example.demo.Discount;

import com.example.demo.Products.Product;
import com.example.demo.Products.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscountService {

    private final DiscountRepository discountRepository;
    private final ProductRepository productRepository;

    public DiscountService(DiscountRepository discountRepository,
                           ProductRepository productRepository) {
        this.discountRepository = discountRepository;
        this.productRepository = productRepository;
    }

    // ✅ CREATE
    public DiscountResponse create(CreateDiscountRequest req) {

        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() ->
                        new RuntimeException("Product not found: " + req.getProductId())
                );

        Discount discount = new Discount();
        discount.setProduct(product);
        discount.setDiscountPercent(req.getDiscountPercent());
        discount.setNote(req.getNote());
        discount.setActive(req.getActive() != null ? req.getActive() : true);

        Discount saved = discountRepository.save(discount);
        return toResponse(saved);
    }

    // ✅ GET ALL
    public List<DiscountResponse> getAll() {
        return discountRepository.findAllWithProductOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ✅ GET ACTIVE
    public List<DiscountResponse> getActive() {
        return discountRepository.findActiveWithProduct()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ✅ SET ACTIVE / INACTIVE
    public DiscountResponse setActive(Long discountId, boolean active) {
        Discount discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new RuntimeException("Discount not found: " + discountId));

        discount.setActive(active);
        Discount saved = discountRepository.save(discount);
        return toResponse(saved);
    }

    // ✅ UPDATE (EDIT) — matches controller: PUT /api/discounts/{id}
    public DiscountResponse update(Long id, CreateDiscountRequest req) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Discount not found: " + id));

        // If productId is provided, allow changing the product link
        if (req.getProductId() != null) {
            Product product = productRepository.findById(req.getProductId())
                    .orElseThrow(() ->
                            new RuntimeException("Product not found: " + req.getProductId())
                    );
            discount.setProduct(product);
        }

        discount.setDiscountPercent(req.getDiscountPercent());
        discount.setNote(req.getNote());

        // if null, keep previous active value (don’t overwrite)
        if (req.getActive() != null) {
            discount.setActive(req.getActive());
        }

        Discount saved = discountRepository.save(discount);
        return toResponse(saved);
    }

    // ✅ DELETE — matches controller: DELETE /api/discounts/{id}
    public void delete(Long id) {
        if (!discountRepository.existsById(id)) {
            throw new RuntimeException("Discount not found: " + id);
        }
        discountRepository.deleteById(id);
    }

    private DiscountResponse toResponse(Discount d) {
        return new DiscountResponse(
                d.getId(),
                d.getProduct().getId(),
                d.getProduct().getName(), // change if your Product field isn't "name"
                d.getDiscountPercent(),
                d.getNote(),
                d.getActive(),
                d.getCreatedAt()
        );
    }
}
