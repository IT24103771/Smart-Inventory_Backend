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

    public List<DiscountResponse> getAll() {
        return discountRepository.findAllWithProductOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<DiscountResponse> getActive() {
        return discountRepository.findActiveWithProduct()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public DiscountResponse setActive(Long discountId, boolean active) {
        Discount discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new RuntimeException("Discount not found: " + discountId));

        discount.setActive(active);
        Discount saved = discountRepository.save(discount);

        return toResponse(saved);
    }

    private DiscountResponse toResponse(Discount d) {
        return new DiscountResponse(
                d.getId(),
                d.getProduct().getId(),
                d.getProduct().getName(), // ⚠️ change if your Product field is not "name"
                d.getDiscountPercent(),
                d.getNote(),
                d.getActive(),
                d.getCreatedAt()
        );
    }
}
