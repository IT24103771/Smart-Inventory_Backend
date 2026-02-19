package com.example.demo.Discount;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discounts")
@CrossOrigin(origins = "http://localhost:3000")
public class DiscountController {

    private final DiscountService discountService;

    public DiscountController(DiscountService discountService) {
        this.discountService = discountService;
    }

    // ✅ CREATE
    @PostMapping
    public DiscountResponse create(@Valid @RequestBody CreateDiscountRequest req) {
        return discountService.create(req);
    }

    // ✅ GET ALL
    @GetMapping
    public List<DiscountResponse> getAll() {
        return discountService.getAll();
    }

    // ✅ GET ACTIVE
    @GetMapping("/active")
    public List<DiscountResponse> getActive() {
        return discountService.getActive();
    }

    // ✅ SET ACTIVE / INACTIVE
    @PatchMapping("/{id}/active")
    public DiscountResponse setActive(@PathVariable Long id, @RequestParam boolean value) {
        return discountService.setActive(id, value);
    }

    // ✅ NEW: UPDATE (EDIT)
    @PutMapping("/{id}")
    public DiscountResponse update(
            @PathVariable Long id,
            @Valid @RequestBody CreateDiscountRequest req
    ) {
        return discountService.update(id, req);
    }

    // ✅ NEW: DELETE
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        discountService.delete(id);
    }
}
