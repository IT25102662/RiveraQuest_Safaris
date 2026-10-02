package com.boatsafari.controller;

import com.boatsafari.model.Promotion;
import com.boatsafari.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promotions")
@CrossOrigin(origins = "*")
public class PromotionController {

    @Autowired
    private PromotionService promotionService;

    @PostMapping
    public ResponseEntity<Promotion> createPromotion(@RequestParam(required = false) Long marketingOfficerId, @RequestBody Promotion promotion) {
        return new ResponseEntity<>(promotionService.createPromotion(marketingOfficerId, promotion), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Promotion>> getAllPromotions() {
        return ResponseEntity.ok(promotionService.getAllPromotions());
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Promotion>> searchPromotions(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String status) {
        return ResponseEntity.ok(promotionService.searchPromotions(keyword, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Promotion> getPromotionById(@PathVariable Long id) {
        return ResponseEntity.ok(promotionService.getPromotionById(id));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<Promotion> getPromotionByCode(@PathVariable String code) {
        return ResponseEntity.ok(promotionService.getPromotionByCode(code));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Promotion> updatePromotion(@PathVariable Long id, @RequestBody Promotion promotion) {
        return ResponseEntity.ok(promotionService.updatePromotion(id, promotion));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePromotion(@PathVariable Long id) {
        promotionService.deletePromotion(id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{id}/trips")
    public ResponseEntity<Promotion> setApplicableTrips(@PathVariable Long id, @RequestBody List<Long> tripIds) {
        return ResponseEntity.ok(promotionService.setApplicableTrips(id, tripIds));
    }

    @GetMapping("/{id}/trips")
    public ResponseEntity<List<Long>> getApplicableTrips(@PathVariable Long id) {
        return ResponseEntity.ok(promotionService.getApplicableTripIds(id));
    }
}
