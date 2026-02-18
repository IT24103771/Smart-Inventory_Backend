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

    @PostMapping
    public DiscountResponse create(@Valid @RequestBody CreateDiscountRequest req) {
        return discountService.create(req);
    }

    @GetMapping
    public List<DiscountResponse> getAll() {
        return discountService.getAll();
    }

    @GetMapping("/active")
    public List<DiscountResponse> getActive() {
        return discountService.getActive();
    }

    @PatchMapping("/{id}/active")
    public DiscountResponse setActive(@PathVariable Long id, @RequestParam boolean value) {
        return discountService.setActive(id, value);
    }
}
