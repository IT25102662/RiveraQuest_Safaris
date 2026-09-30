package com.boatsafari.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "promotions")
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType = DiscountType.PERCENTAGE;

    private Double discountPercentage; // used when discountType = PERCENTAGE

    private Double fixedAmount; // used when discountType = FIXED_AMOUNT

    private Double maxDiscount; // optional cap on percentage discounts

    private Double minBookingAmount; // optional minimum spend to qualify

    private LocalDate validFrom;

    private LocalDate validUntil;

    @Column(nullable = false)
    private Boolean active = true;

    private Integer usageCount = 0;

    private Integer usageLimit; // null = unlimited

    @Enumerated(EnumType.STRING)
    private VoucherStatus status = VoucherStatus.ACTIVE;

    // Report-required fields, added alongside your existing usage tracking & auto-computed status
    @Column(length = 10)
    private String reportStatus = "Draft"; // Draft / Published / Archived — set manually by Marketing Officer

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private MarketingOfficer createdBy;

    public Promotion() {}

    public Promotion(Long id, String code, String description, Double discountPercentage, LocalDate validFrom, LocalDate validUntil, Boolean active, Integer usageCount) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.discountPercentage = discountPercentage;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.active = active;
        this.usageCount = usageCount;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public DiscountType getDiscountType() { return discountType; }
    public void setDiscountType(DiscountType discountType) { this.discountType = discountType; }

    public Double getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(Double discountPercentage) { this.discountPercentage = discountPercentage; }

    public Double getFixedAmount() { return fixedAmount; }
    public void setFixedAmount(Double fixedAmount) { this.fixedAmount = fixedAmount; }

    public Double getMaxDiscount() { return maxDiscount; }
    public void setMaxDiscount(Double maxDiscount) { this.maxDiscount = maxDiscount; }

    public Double getMinBookingAmount() { return minBookingAmount; }
    public void setMinBookingAmount(Double minBookingAmount) { this.minBookingAmount = minBookingAmount; }

    public LocalDate getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDate validFrom) { this.validFrom = validFrom; }

    public LocalDate getValidUntil() { return validUntil; }
    public void setValidUntil(LocalDate validUntil) { this.validUntil = validUntil; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public Integer getUsageCount() { return usageCount; }
    public void setUsageCount(Integer usageCount) { this.usageCount = usageCount; }

    public Integer getUsageLimit() { return usageLimit; }
    public void setUsageLimit(Integer usageLimit) { this.usageLimit = usageLimit; }

    public VoucherStatus getStatus() { return status; }
    public void setStatus(VoucherStatus status) { this.status = status; }

    public String getReportStatus() { return reportStatus; }
    public void setReportStatus(String reportStatus) { this.reportStatus = reportStatus; }

    public MarketingOfficer getCreatedBy() { return createdBy; }
    public void setCreatedBy(MarketingOfficer createdBy) { this.createdBy = createdBy; }

    @Transient
    public VoucherStatus getComputedStatus() {
        if (status == VoucherStatus.INACTIVE) return VoucherStatus.INACTIVE;
        if (validUntil != null && validUntil.isBefore(LocalDate.now())) return VoucherStatus.EXPIRED;
        return VoucherStatus.ACTIVE;
    }
}