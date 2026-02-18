package com.example.demo.Inventory;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "http://localhost:3000")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    public InventoryResponse create(@Valid @RequestBody CreateInventoryRequest req) {
        return inventoryService.create(req);
    }

    @GetMapping
    public List<InventoryResponse> getAll() {
        return inventoryService.getAll();
    }

    @PutMapping("/{id}")
    public InventoryResponse update(@PathVariable Long id, @Valid @RequestBody CreateInventoryRequest req) {
        return inventoryService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        inventoryService.delete(id);
    }
}
