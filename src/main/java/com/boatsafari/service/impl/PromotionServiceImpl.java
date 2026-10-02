package com.boatsafari.service.impl;

import com.boatsafari.exception.BusinessRuleException;
import com.boatsafari.exception.ResourceNotFoundException;
import com.boatsafari.model.DiscountType;
import com.boatsafari.model.MarketingOfficer;
import com.boatsafari.model.Promotion;
import com.boatsafari.model.Trip;
import com.boatsafari.model.VoucherStatus;
import com.boatsafari.repository.MarketingOfficerRepository;
import com.boatsafari.repository.PromotionRepository;
import com.boatsafari.repository.TripRepository;
import com.boatsafari.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PromotionServiceImpl implements PromotionService {
    @Autowired
    private PromotionRepository promotionRepository;
    
    @Autowired
    private MarketingOfficerRepository marketingOfficerRepository;
    
    @Autowired
    private TripRepository tripRepository;
    
    private void validate(Promotion promotion) {
        if (promotion.getCode() == null || promotion.getCode().trim().isEmpty()) {
            throw new BusinessRuleException("Voucher code is required.");
        }
        if (promotion.getTitle() == null || promotion.getTitle().trim().isEmpty()) {
            throw new BusinessRuleException("Voucher title is required.");
        }
        if (promotion.getValidFrom() == null || promotion.getValidUntil() == null) {
            throw new BusinessRuleException("Start date and expiry date are required.");
        }
        if (promotion.getValidUntil().isBefore(promotion.getValidFrom())) {
            throw new BusinessRuleException("Expiry date cannot be before the start date.");
        }
        DiscountType type = promotion.getDiscountType() == null ? DiscountType.PERCENTAGE : promotion.getDiscountType();
        if (type == DiscountType.PERCENTAGE) {
            if (promotion.getDiscountPercentage() == null || promotion.getDiscountPercentage() <= 0 || promotion.getDiscountPercentage() > 100) {
                throw new BusinessRuleException("Discount percentage must be between 1 and 100.");
            }
        } else {
            if (promotion.getFixedAmount() == null || promotion.getFixedAmount() <= 0) {
                throw new BusinessRuleException("Fixed discount amount must be greater than zero.");
            }
        }
        if (promotion.getUsageLimit() != null && promotion.getUsageLimit() < 0) {
            throw new BusinessRuleException("Usage limit cannot be negative.");
        }
    }
    @Override
    public Promotion createPromotion(Long marketingOfficerId, Promotion promotion) {
        validate(promotion);
        if (promotionRepository.findByCodeIgnoreCase(promotion.getCode().trim()).isPresent()) {
            throw new BusinessRuleException("Promotion code '" + promotion.getCode() + "' already exists!");
        }
        if (promotion.getDiscountType() == null) promotion.setDiscountType(DiscountType.PERCENTAGE);
        if (promotion.getStatus() == null) promotion.setStatus(VoucherStatus.ACTIVE);
        promotion.setActive(promotion.getStatus() != VoucherStatus.INACTIVE);
        promotion.setUsageCount(0);
        if (promotion.getReportStatus() == null) promotion.setReportStatus("Draft");

        MarketingOfficer officer;
        if (marketingOfficerId != null) {
            officer = marketingOfficerRepository.findById(marketingOfficerId)
                .orElseThrow(() -> new ResourceNotFoundException("Marketing Officer not found with ID: " + marketingOfficerId));
        } else {
            officer = marketingOfficerRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new BusinessRuleException("No Marketing Officer on record to attribute this promotion to."));
        }
        promotion.setCreatedBy(officer);

        return promotionRepository.save(promotion);
    }
    @Override
    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }
    
    @Override
    public Promotion getPromotionById(Long id) {
        return promotionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Promotion not found with ID: " + id));
    }
    
    @Override
    public Promotion getPromotionByCode(String code) {
        return promotionRepository.findByCodeIgnoreCase(code)
            .orElseThrow(() -> new ResourceNotFoundException("Promotion code not found: " + code));
    }

    @Override
    public Promotion updatePromotion(Long id, Promotion promotion) {
        Promotion existing = promotionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Promotion not found with ID: " + id));
        
        validate(promotion);

        Optional<Promotion> codeOwner = promotionRepository.findByCodeIgnoreCase(promotion.getCode().trim());
        if (codeOwner.isPresent() && !codeOwner.get().getId().equals(id)) {
            throw new BusinessRuleException("Promotion code '" + promotion.getCode() + "' is already used by another voucher!");
        }
        
        existing.setCode(promotion.getCode().trim());
        existing.setTitle(promotion.getTitle());
        existing.setDescription(promotion.getDescription());
        existing.setDiscountType(promotion.getDiscountType() == null ? DiscountType.PERCENTAGE : promotion.getDiscountType());
        existing.setDiscountPercentage(promotion.getDiscountPercentage());
        existing.setFixedAmount(promotion.getFixedAmount());
        existing.setMaxDiscount(promotion.getMaxDiscount());
        existing.setMinBookingAmount(promotion.getMinBookingAmount());
        existing.setValidFrom(promotion.getValidFrom());
        existing.setValidUntil(promotion.getValidUntil());
        existing.setUsageLimit(promotion.getUsageLimit());
        existing.setStatus(promotion.getStatus() == null ? VoucherStatus.ACTIVE : promotion.getStatus());
        existing.setActive(existing.getStatus() != VoucherStatus.INACTIVE);
        
        if (promotion.getReportStatus() != null) {
            existing.setReportStatus(promotion.getReportStatus());
        }
        return promotionRepository.save(existing);
    }
    @Override
    public void deletePromotion(Long id) {
        Promotion existing = promotionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Promotion not found with ID: " + id));
        if (existing.getUsageCount() != null && existing.getUsageCount() > 0) {
            existing.setStatus(VoucherStatus.INACTIVE);
            existing.setActive(false);
            promotionRepository.save(existing);
        } else {
            promotionRepository.delete(existing);
        }
    }
    
    @Override
    public List<Promotion> searchPromotions(String keyword, String statusFilter) {
        String kw = keyword == null ? "" : keyword.trim().toLowerCase();
        return promotionRepository.findAll().stream()
            .filter(p -> kw.isEmpty()
                    || (p.getCode() != null && p.getCode().toLowerCase().contains(kw))
                    || (p.getTitle() != null && p.getTitle().toLowerCase().contains(kw)))
            .filter(p -> statusFilter == null || statusFilter.isEmpty() || "ALL".equalsIgnoreCase(statusFilter)
                    || p.getComputedStatus().name().equalsIgnoreCase(statusFilter))
             .toList();
    }
    @Override
    public Promotion setApplicableTrips(Long promotionId, List<Long> tripIds) {
        Promotion promotion = getPromotionById(promotionId);
        if (tripIds == null || tripIds.isEmpty()) {
            promotion.setApplicableTrips(new HashSet<>());
        } else {
            Set<Trip> trips = tripIds.stream()
                .map(id -> tripRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + id)))
                .collect(Collectors.toSet());
            promotion.setApplicableTrips(trips);
        }
        return promotionRepository.save(promotion);
    }
    @Override
    public List<Long> getApplicableTripIds(Long promotionId) {
        Promotion promotion = getPromotionById(promotionId);
        return promotion.getApplicableTrips().stream().map(Trip::getId).collect(Collectors.toList());
    }

    @Override
    public double calculateDiscount(Promotion promo, Trip trip, double totalPrice) {
        String code = promo.getCode();
        LocalDate today = LocalDate.now();

        if (promo.getStatus() == VoucherStatus.INACTIVE || !Boolean.TRUE.equals(promo.getActive())) {
            throw new BusinessRuleException("Voucher code '" + code + "' is no longer active!");
        }
        if (promo.getValidFrom() != null && today.isBefore(promo.getValidFrom())) {
            throw new BusinessRuleException("Voucher code '" + code + "' is not valid until " + promo.getValidFrom() + ".");
        }
        if (promo.getValidUntil() != null && today.isAfter(promo.getValidUntil())) {
            throw new BusinessRuleException("Voucher code '" + code + "' expired on " + promo.getValidUntil() + ".");
        }
        int used = promo.getUsageCount() == null ? 0 : promo.getUsageCount();
        if (promo.getUsageLimit() != null && promo.getUsageLimit() > 0 && used >= promo.getUsageLimit()) {
            throw new BusinessRuleException("Voucher code '" + code + "' has reached its usage limit.");
        }
        if (!promo.getApplicableTrips().isEmpty()
                && promo.getApplicableTrips().stream().noneMatch(t -> t.getId().equals(trip.getId()))) {
            throw new BusinessRuleException("Voucher code '" + code + "' is not valid for this trip.");
        }
        if (promo.getMinBookingAmount() != null && totalPrice < promo.getMinBookingAmount()) {
            throw new BusinessRuleException("Voucher code '" + code + "' requires a minimum booking of LKR "
                    + String.format("%,.0f", promo.getMinBookingAmount()) + ".");
        }

        double discount;
        if (promo.getDiscountType() == DiscountType.FIXED_AMOUNT) {
            discount = promo.getFixedAmount() == null ? 0.0 : promo.getFixedAmount();
        } else {
            double pct = promo.getDiscountPercentage() == null ? 0.0 : promo.getDiscountPercentage();
            discount = totalPrice * pct / 100.0;
            if (promo.getMaxDiscount() != null && promo.getMaxDiscount() > 0) {
                discount = Math.min(discount, promo.getMaxDiscount());
            }
        }
        return Math.min(discount, totalPrice);
    }
}
