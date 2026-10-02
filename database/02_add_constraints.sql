-- =====================================================================
-- RiveraQuest Safaris - Step 3: add the PDF's CHECK / UNIQUE rules
-- Safe to run more than once. Each rule is added only if it does not exist yet
-- AND no existing row breaks it. Nothing is deleted or changed in your data.
-- Run in SSMS (BoatSafariDB), then send the Messages tab.
-- =====================================================================
USE BoatSafariDB;
GO
SET NOCOUNT ON;

-- Staff: staffType must be one of the five roles
IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE name = 'CK_staff_staffType')
BEGIN
    IF EXISTS (SELECT 1 FROM staff WHERE NOT (staff_type IN ('BookingOfficer','FleetManager','SafetyOfficer','MarketingOfficer','Administrator')))
        PRINT 'SKIPPED  CK_staff_staffType: existing rows in staff break this rule (see the pre-check at the end)';
    ELSE
    BEGIN
        ALTER TABLE staff ADD CONSTRAINT CK_staff_staffType CHECK (staff_type IN ('BookingOfficer','FleetManager','SafetyOfficer','MarketingOfficer','Administrator'));
        PRINT 'ADDED    CK_staff_staffType';
    END
END
ELSE PRINT 'EXISTS   CK_staff_staffType';

-- Boat: capacity > 0
IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE name = 'CK_boats_capacity')
BEGIN
    IF EXISTS (SELECT 1 FROM boats WHERE NOT (capacity > 0))
        PRINT 'SKIPPED  CK_boats_capacity: existing rows in boats break this rule (see the pre-check at the end)';
    ELSE
    BEGIN
        ALTER TABLE boats ADD CONSTRAINT CK_boats_capacity CHECK (capacity > 0);
        PRINT 'ADDED    CK_boats_capacity';
    END
END
ELSE PRINT 'EXISTS   CK_boats_capacity';

-- Boat: safetyStatus is Cleared or Not Cleared
IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE name = 'CK_boats_safetyStatus')
BEGIN
    IF EXISTS (SELECT 1 FROM boats WHERE NOT (safety_status IN ('Cleared','Not Cleared')))
        PRINT 'SKIPPED  CK_boats_safetyStatus: existing rows in boats break this rule (see the pre-check at the end)';
    ELSE
    BEGIN
        ALTER TABLE boats ADD CONSTRAINT CK_boats_safetyStatus CHECK (safety_status IN ('Cleared','Not Cleared'));
        PRINT 'ADDED    CK_boats_safetyStatus';
    END
END
ELSE PRINT 'EXISTS   CK_boats_safetyStatus';

-- Booking: seatCount > 0
IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE name = 'CK_bookings_seatCount')
BEGIN
    IF EXISTS (SELECT 1 FROM bookings WHERE NOT (seat_count > 0))
        PRINT 'SKIPPED  CK_bookings_seatCount: existing rows in bookings break this rule (see the pre-check at the end)';
    ELSE
    BEGIN
        ALTER TABLE bookings ADD CONSTRAINT CK_bookings_seatCount CHECK (seat_count > 0);
        PRINT 'ADDED    CK_bookings_seatCount';
    END
END
ELSE PRINT 'EXISTS   CK_bookings_seatCount';

-- Booking: amounts >= 0
IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE name = 'CK_bookings_totalPrice')
BEGIN
    IF EXISTS (SELECT 1 FROM bookings WHERE NOT (total_price >= 0 AND final_price >= 0))
        PRINT 'SKIPPED  CK_bookings_totalPrice: existing rows in bookings break this rule (see the pre-check at the end)';
    ELSE
    BEGIN
        ALTER TABLE bookings ADD CONSTRAINT CK_bookings_totalPrice CHECK (total_price >= 0 AND final_price >= 0);
        PRINT 'ADDED    CK_bookings_totalPrice';
    END
END
ELSE PRINT 'EXISTS   CK_bookings_totalPrice';

-- Promotion: percentage above 0 and at most 50
IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE name = 'CK_promotions_discountPercentage')
BEGIN
    IF EXISTS (SELECT 1 FROM promotions WHERE NOT (discount_percentage IS NULL OR (discount_percentage > 0 AND discount_percentage <= 50)))
        PRINT 'SKIPPED  CK_promotions_discountPercentage: existing rows in promotions break this rule (see the pre-check at the end)';
    ELSE
    BEGIN
        ALTER TABLE promotions ADD CONSTRAINT CK_promotions_discountPercentage CHECK (discount_percentage IS NULL OR (discount_percentage > 0 AND discount_percentage <= 50));
        PRINT 'ADDED    CK_promotions_discountPercentage';
    END
END
ELSE PRINT 'EXISTS   CK_promotions_discountPercentage';

-- Promotion: fixed amount > 0
IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE name = 'CK_promotions_fixedAmount')
BEGIN
    IF EXISTS (SELECT 1 FROM promotions WHERE NOT (fixed_amount IS NULL OR fixed_amount > 0))
        PRINT 'SKIPPED  CK_promotions_fixedAmount: existing rows in promotions break this rule (see the pre-check at the end)';
    ELSE
    BEGIN
        ALTER TABLE promotions ADD CONSTRAINT CK_promotions_fixedAmount CHECK (fixed_amount IS NULL OR fixed_amount > 0);
        PRINT 'ADDED    CK_promotions_fixedAmount';
    END
END
ELSE PRINT 'EXISTS   CK_promotions_fixedAmount';

-- Promotion: validTo after validFrom
IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE name = 'CK_promotions_dates')
BEGIN
    IF EXISTS (SELECT 1 FROM promotions WHERE NOT (valid_until > valid_from))
        PRINT 'SKIPPED  CK_promotions_dates: existing rows in promotions break this rule (see the pre-check at the end)';
    ELSE
    BEGIN
        ALTER TABLE promotions ADD CONSTRAINT CK_promotions_dates CHECK (valid_until > valid_from);
        PRINT 'ADDED    CK_promotions_dates';
    END
END
ELSE PRINT 'EXISTS   CK_promotions_dates';

-- Promotion: the chosen discount type must have its value
IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE name = 'CK_promotions_oneDiscount')
BEGIN
    IF EXISTS (SELECT 1 FROM promotions WHERE NOT ((discount_type = 'PERCENTAGE' AND discount_percentage IS NOT NULL) OR (discount_type = 'FIXED_AMOUNT' AND fixed_amount IS NOT NULL)))
        PRINT 'SKIPPED  CK_promotions_oneDiscount: existing rows in promotions break this rule (see the pre-check at the end)';
    ELSE
    BEGIN
        ALTER TABLE promotions ADD CONSTRAINT CK_promotions_oneDiscount CHECK ((discount_type = 'PERCENTAGE' AND discount_percentage IS NOT NULL) OR (discount_type = 'FIXED_AMOUNT' AND fixed_amount IS NOT NULL));
        PRINT 'ADDED    CK_promotions_oneDiscount';
    END
END
ELSE PRINT 'EXISTS   CK_promotions_oneDiscount';

-- Review: rating between 1 and 5
IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE name = 'CK_reviews_rating')
BEGIN
    IF EXISTS (SELECT 1 FROM reviews WHERE NOT (rating BETWEEN 1 AND 5))
        PRINT 'SKIPPED  CK_reviews_rating: existing rows in reviews break this rule (see the pre-check at the end)';
    ELSE
    BEGIN
        ALTER TABLE reviews ADD CONSTRAINT CK_reviews_rating CHECK (rating BETWEEN 1 AND 5);
        PRINT 'ADDED    CK_reviews_rating';
    END
END
ELSE PRINT 'EXISTS   CK_reviews_rating';

-- MaintenanceLog: cost >= 0
IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE name = 'CK_maintenance_logs_cost')
BEGIN
    IF EXISTS (SELECT 1 FROM maintenance_logs WHERE NOT (cost IS NULL OR cost >= 0))
        PRINT 'SKIPPED  CK_maintenance_logs_cost: existing rows in maintenance_logs break this rule (see the pre-check at the end)';
    ELSE
    BEGIN
        ALTER TABLE maintenance_logs ADD CONSTRAINT CK_maintenance_logs_cost CHECK (cost IS NULL OR cost >= 0);
        PRINT 'ADDED    CK_maintenance_logs_cost';
    END
END
ELSE PRINT 'EXISTS   CK_maintenance_logs_cost';

-- Staff: UNIQUE (staffID, staffType) as in the PDF (supports the disjoint ISA rule)
IF NOT EXISTS (SELECT 1 FROM sys.key_constraints WHERE name = 'UQ_staff_id_staffType')
BEGIN
    ALTER TABLE staff ADD CONSTRAINT UQ_staff_id_staffType UNIQUE (id, staff_type);
    PRINT 'ADDED    UQ_staff_id_staffType';
END
ELSE PRINT 'EXISTS   UQ_staff_id_staffType';

-- Review: one review per customer per trip
IF NOT EXISTS (SELECT 1 FROM sys.key_constraints WHERE name = 'UQ_reviews_customer_trip')
BEGIN
    IF EXISTS (SELECT 1 FROM reviews GROUP BY customer_id, trip_id HAVING COUNT(*) > 1)
        PRINT 'SKIPPED  UQ_reviews_customer_trip: duplicate reviews exist (see the pre-check at the end)';
    ELSE
    BEGIN
        ALTER TABLE reviews ADD CONSTRAINT UQ_reviews_customer_trip UNIQUE (customer_id, trip_id);
        PRINT 'ADDED    UQ_reviews_customer_trip';
    END
END
ELSE PRINT 'EXISTS   UQ_reviews_customer_trip';
GO

-- ---------------------------------------------------------------------
-- PRE-CHECK: rows that would stop a SKIPPED rule from being added (empty = nothing to fix)
-- ---------------------------------------------------------------------
SELECT 'promotions' AS tableName, id, code AS ref, discount_type, discount_percentage, fixed_amount, valid_from, valid_until
FROM promotions
WHERE NOT ((discount_percentage IS NULL OR (discount_percentage > 0 AND discount_percentage <= 50))
       AND (fixed_amount IS NULL OR fixed_amount > 0)
       AND valid_until > valid_from
       AND ((discount_type = 'PERCENTAGE' AND discount_percentage IS NOT NULL)
         OR (discount_type = 'FIXED_AMOUNT' AND fixed_amount IS NOT NULL)));

SELECT 'reviews (duplicates)' AS tableName, customer_id, trip_id, COUNT(*) AS copies
FROM reviews GROUP BY customer_id, trip_id HAVING COUNT(*) > 1;
GO
