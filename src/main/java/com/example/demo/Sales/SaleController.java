package com.example.demo.Sales;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
@CrossOrigin(origins = "http://localhost:3000")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping
    public SaleResponse create(@Valid @RequestBody CreateSaleRequest req) {
        return saleService.create(req);
    }

    @GetMapping
    public List<SaleResponse> getAll() {
        return saleService.getAll();
    }

    @PutMapping("/{id}")
    public SaleResponse update(@PathVariable Long id, @Valid @RequestBody CreateSaleRequest req) {
        return saleService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        saleService.delete(id);
    }
}
