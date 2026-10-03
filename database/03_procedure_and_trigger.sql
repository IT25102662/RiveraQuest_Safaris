-- =====================================================================
-- RiveraQuest Safaris - Step 4: stored procedure usp_BookTrip + audit trigger
-- (Assignment 01 Part 02, Part E and Part F, adapted to the table and column
--  names that the application uses: bookings, trips, promotions, audit_logs ...)
--
-- Run in SSMS with BoatSafariDB selected. Safe to run more than once
-- (CREATE OR ALTER). Nothing in your data is changed by running this script.
-- If a booking made on the website fails after this, run the ROLLBACK block
-- at the bottom of this file.
-- =====================================================================
USE BoatSafariDB;
GO

-- ---------------------------------------------------------------------
-- Part E: usp_BookTrip
-- Books seats on a trip for a customer. It checks that the customer is Active,
-- that the trip is Scheduled and that enough seats are free. If a voucher code
-- is given it checks that the voucher is active, valid today, valid for the trip
-- and within its usage limit and minimum amount. Then it works out the amounts,
-- picks the lowest free seat numbers (S-01, S-02 ...), saves the booking as
-- PENDING and returns it. If any check fails nothing is saved and an error is
-- raised. The trip row is locked while it runs, so two users cannot take the
-- last seats together.
-- ---------------------------------------------------------------------
CREATE OR ALTER PROCEDURE dbo.usp_BookTrip
    @customerId       BIGINT,
    @tripId           BIGINT,
    @seatCount        INT,
    @voucherCode      VARCHAR(30) = NULL,
    @bookingOfficerId BIGINT      = NULL
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    DECLARE @capacity INT, @price FLOAT, @tripStatus VARCHAR(15),
            @seatsTaken INT, @available INT,
            @total FLOAT, @discount FLOAT = 0, @final FLOAT,
            @promotionId BIGINT = NULL,
            @discType VARCHAR(20), @pct FLOAT, @fixed FLOAT, @maxDisc FLOAT, @minAmt FLOAT,
            @validFrom DATE, @validUntil DATE, @promoActive BIT, @promoStatus VARCHAR(20),
            @usageCount INT, @usageLimit INT,
            @seats NVARCHAR(400), @ref NVARCHAR(50), @newId BIGINT;

    BEGIN TRY
        BEGIN TRANSACTION;

        -- 1. Check the input
        IF @seatCount IS NULL OR @seatCount <= 0
            THROW 50001, 'The seat count must be at least 1.', 1;

        IF NOT EXISTS (SELECT 1 FROM customers WHERE id = @customerId AND account_status = 'Active')
            THROW 50002, 'The customer was not found or the account is not active.', 1;

        IF @bookingOfficerId IS NOT NULL
           AND NOT EXISTS (SELECT 1 FROM booking_officers WHERE id = @bookingOfficerId)
            THROW 50009, 'The booking officer was not found.', 1;

        -- 2. Read the trip (the row is locked so two users cannot take the last seats together)
        SELECT @capacity = passenger_capacity, @price = price, @tripStatus = status
        FROM trips WITH (UPDLOCK, HOLDLOCK)
        WHERE id = @tripId;

        IF @tripStatus IS NULL
            THROW 50003, 'The trip was not found.', 1;
        IF @tripStatus <> 'Scheduled'
            THROW 50004, 'The trip is not open for booking.', 1;

        -- 3. Check the free seats (cancelled bookings do not use seats)
        SELECT @seatsTaken = ISNULL(SUM(seat_count), 0)
        FROM bookings
        WHERE trip_id = @tripId AND UPPER(status) <> 'CANCELLED';

        SET @available = @capacity - @seatsTaken;
        IF @seatCount > @available
            THROW 50005, 'Not enough free seats on this trip.', 1;

        SET @total = ROUND(@price * @seatCount, 2);

        -- 4. Voucher (optional)
        IF @voucherCode IS NOT NULL AND LTRIM(RTRIM(@voucherCode)) <> ''
        BEGIN
            SELECT @promotionId = id, @discType = discount_type, @pct = discount_percentage,
                   @fixed = fixed_amount, @maxDisc = max_discount, @minAmt = min_booking_amount,
                   @validFrom = valid_from, @validUntil = valid_until, @promoActive = active,
                   @promoStatus = status, @usageCount = ISNULL(usage_count, 0), @usageLimit = usage_limit
            FROM promotions WITH (UPDLOCK)
            WHERE UPPER(code) = UPPER(LTRIM(RTRIM(@voucherCode)));

            IF @promotionId IS NULL
                THROW 50006, 'The voucher code was not found.', 1;
            IF @promoActive = 0 OR @promoStatus <> 'ACTIVE'
                THROW 50006, 'The voucher is not active.', 1;
            IF CAST(GETDATE() AS DATE) < @validFrom OR CAST(GETDATE() AS DATE) > @validUntil
                THROW 50006, 'The voucher is not valid today.', 1;
            IF @usageLimit IS NOT NULL AND @usageCount >= @usageLimit
                THROW 50006, 'The voucher has reached its usage limit.', 1;
            IF EXISTS (SELECT 1 FROM promotion_trips WHERE promotion_id = @promotionId)
               AND NOT EXISTS (SELECT 1 FROM promotion_trips WHERE promotion_id = @promotionId AND trip_id = @tripId)
                THROW 50006, 'The voucher is not valid for this trip.', 1;
            IF @minAmt IS NOT NULL AND @total < @minAmt
                THROW 50006, 'The booking amount is below the voucher minimum.', 1;

            IF @discType = 'PERCENTAGE'
            BEGIN
                SET @discount = ROUND(@total * @pct / 100.0, 2);
                IF @maxDisc IS NOT NULL AND @discount > @maxDisc SET @discount = @maxDisc;
            END
            ELSE
                SET @discount = ISNULL(@fixed, 0);

            IF @discount > @total SET @discount = @total;
        END

        SET @final = @total - @discount;

        -- 5. Choose the lowest free seat numbers (format S-01, S-02 ...)
        DECLARE @taken TABLE (n INT);
        INSERT INTO @taken (n)
        SELECT TRY_CAST(SUBSTRING(LTRIM(RTRIM(s.value)), 3, 10) AS INT)
        FROM bookings b
        CROSS APPLY STRING_SPLIT(b.seat_numbers, ',') s
        WHERE b.trip_id = @tripId AND UPPER(b.status) <> 'CANCELLED' AND b.seat_numbers IS NOT NULL;

        ;WITH nums AS (
            SELECT 1 AS n
            UNION ALL
            SELECT n + 1 FROM nums WHERE n < @capacity
        )
        SELECT @seats = STRING_AGG(lbl, ', ') WITHIN GROUP (ORDER BY n)
        FROM (
            SELECT TOP (@seatCount) n, 'S-' + RIGHT('0' + CAST(n AS VARCHAR(3)), 2) AS lbl
            FROM nums
            WHERE n NOT IN (SELECT n FROM @taken WHERE n IS NOT NULL)
            ORDER BY n
        ) pick
        OPTION (MAXRECURSION 1000);

        -- 6. Unique booking reference BK-<year>-<4 digits>
        WHILE 1 = 1
        BEGIN
            SET @ref = 'BK-' + CAST(YEAR(GETDATE()) AS VARCHAR(4)) + '-'
                     + CAST(1000 + ABS(CHECKSUM(NEWID())) % 9000 AS VARCHAR(4));
            IF NOT EXISTS (SELECT 1 FROM bookings WHERE booking_reference = @ref) BREAK;
        END

        -- 7. Save the booking as PENDING
        INSERT INTO bookings (booking_reference, customer_id, trip_id, seat_count, seat_numbers,
                              total_price, discount_amount, final_price, booking_date, status,
                              validation_status, payment_method, promotion_id, booking_officer_id)
        VALUES (@ref, @customerId, @tripId, @seatCount, @seats,
                @total, @discount, @final, SYSDATETIME(), 'PENDING',
                'PENDING', 'ONLINE_CARD', @promotionId, @bookingOfficerId);
        SET @newId = SCOPE_IDENTITY();

        -- 8. Keep the trip and voucher counters in step
        UPDATE trips SET booked_seats = @seatsTaken + @seatCount WHERE id = @tripId;
        IF @promotionId IS NOT NULL
            UPDATE promotions SET usage_count = ISNULL(usage_count, 0) + 1 WHERE id = @promotionId;

        COMMIT TRANSACTION;

        -- 9. Return the result
        SELECT id AS bookingId, booking_reference AS bookingReference, status, seat_count AS seatCount,
               seat_numbers AS seatNumbers, total_price AS totalPrice, discount_amount AS discountAmount,
               final_price AS finalPrice
        FROM bookings WHERE id = @newId;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END;
GO

-- ---------------------------------------------------------------------
-- Part F: trg_bookings_audit
-- Runs after a booking is inserted or updated. A new booking saves an INSERT
-- row in audit_logs. When the status of a booking changes it saves an UPDATE
-- row with the old and the new status. If the status does not change nothing
-- is saved. It uses the inserted and deleted tables, so it also works when one
-- statement changes many bookings.
-- ---------------------------------------------------------------------
CREATE OR ALTER TRIGGER dbo.trg_bookings_audit
ON dbo.bookings
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    -- 1. New bookings: INSERT record
    INSERT INTO audit_logs (table_name, record_id, action, old_value, new_value, changed_by, changed_at)
    SELECT 'bookings', CAST(i.id AS VARCHAR(50)), 'INSERT', NULL, i.status, SUSER_SNAME(), SYSDATETIME()
    FROM inserted i
    WHERE NOT EXISTS (SELECT 1 FROM deleted d WHERE d.id = i.id);

    -- 2. Changed bookings: UPDATE record only when the status really changed
    INSERT INTO audit_logs (table_name, record_id, action, old_value, new_value, changed_by, changed_at)
    SELECT 'bookings', CAST(i.id AS VARCHAR(50)), 'UPDATE', d.status, i.status, SUSER_SNAME(), SYSDATETIME()
    FROM inserted i
    INNER JOIN deleted d ON d.id = i.id
    WHERE i.status <> d.status;
END;
GO

PRINT 'usp_BookTrip and trg_bookings_audit created.';
GO

/* =====================================================================
   DEMO (run these lines one at a time after the script above; they are in a
   comment so running the whole file does not create any booking)

   -- Before
   SELECT * FROM audit_logs;

   -- Book 1 seat on trip 1 for customer 1 (Pending)
   EXEC dbo.usp_BookTrip @customerId = 1, @tripId = 1, @seatCount = 1;

   -- Book with a voucher from your data, e.g. SAFARI15
   EXEC dbo.usp_BookTrip @customerId = 1, @tripId = 1, @seatCount = 2, @voucherCode = 'SAFARI15';

   -- A failing call: nothing is saved and an error message is returned
   EXEC dbo.usp_BookTrip @customerId = 1, @tripId = 1, @seatCount = 500;

   -- Confirm a booking: the trigger logs the status change (use a real id)
   UPDATE bookings SET status = 'CONFIRMED' WHERE id = (SELECT MAX(id) FROM bookings);

   -- An update that does not change the status: nothing must be logged
   UPDATE bookings SET total_price = total_price WHERE id = 1;

   -- After
   SELECT * FROM audit_logs ORDER BY id DESC;
   ===================================================================== */

/* =====================================================================
   ROLLBACK (only if booking on the website fails after the trigger is created)
   DROP TRIGGER IF EXISTS dbo.trg_bookings_audit;
   DROP PROCEDURE IF EXISTS dbo.usp_BookTrip;
   ===================================================================== */
