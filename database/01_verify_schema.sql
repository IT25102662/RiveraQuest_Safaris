-- =====================================================================
-- RiveraQuest Safaris - Step 1: read-only schema verification
-- Compares BoatSafariDB with the 29-table design in Assignment 01 Part 02.
-- This script only READS metadata. It changes nothing.
-- Run in SSMS with BoatSafariDB selected, then send the three result sets.
-- =====================================================================
USE BoatSafariDB;
GO

-- Expected tables: PDF name -> name used by the application
DECLARE @expected TABLE (pdfTable VARCHAR(40), appTable VARCHAR(40), kind VARCHAR(20));
INSERT INTO @expected VALUES
('Customer','customers','Strong'),('Staff','staff','Strong'),('Boat','boats','Strong'),
('Crew','crews','Strong'),('Route','routes','Strong'),('Guide','guides','Strong'),
('Trip','trips','Strong'),('Promotion','promotions','Strong'),('Report','reports','Strong'),
('EmergencyContact','emergency_contacts','Weak'),('TripSafetyContact','trip_safety_contacts','Weak'),
('MaintenanceLog','maintenance_logs','Weak'),('SafetyChecklist','safety_checklists','Weak'),
('CustomerPhone','customer_phones','Multivalued'),('StaffContactNumber','staff_contact_numbers','Multivalued'),
('GuideLanguage','guide_languages','Multivalued'),('EmergencyContactPhone','emergency_contact_phones','Multivalued'),
('TripSafetyContactPhone','trip_safety_contact_phones','Multivalued'),('SafetyChecklistItem','safety_checklist_items','Multivalued'),
('BookingOfficer','booking_officers','ISA'),('FleetManager','fleet_managers','ISA'),
('SafetyOfficer','safety_officers','ISA'),('MarketingOfficer','marketing_officers','ISA'),
('Administrator','administrators','ISA'),
('Booking','bookings','Relationship'),('Review','reviews','Relationship'),
('CrewAssignment','crew_assignments','Relationship'),('PromotionTrip','promotion_trips','Relationship'),
('AuditLog','audit_logs','Support');

-- RESULT 1: which of the 29 tables exist (PRESENT / MISSING) and their row counts
SELECT e.kind, e.pdfTable, e.appTable,
       CASE WHEN t.object_id IS NULL THEN 'MISSING' ELSE 'PRESENT' END AS state,
       p.row_count AS rowsInTable
FROM @expected e
LEFT JOIN sys.tables t ON t.name = e.appTable
LEFT JOIN (SELECT object_id, SUM(row_count) row_count FROM sys.dm_db_partition_stats
           WHERE index_id IN (0,1) GROUP BY object_id) p ON p.object_id = t.object_id
ORDER BY CASE e.kind WHEN 'Strong' THEN 1 WHEN 'Weak' THEN 2 WHEN 'Multivalued' THEN 3
                     WHEN 'ISA' THEN 4 WHEN 'Relationship' THEN 5 ELSE 6 END, e.pdfTable;

-- RESULT 2: extra tables in the database that are not part of the 29-table design
SELECT t.name AS extraTable
FROM sys.tables t
WHERE t.name NOT IN (SELECT appTable FROM @expected)
ORDER BY t.name;

-- RESULT 3: rules the PDF requires, listed per table (CHECK / UNIQUE / FOREIGN KEY already in the database)
SELECT CAST(OBJECT_NAME(parent_object_id) AS NVARCHAR(128)) COLLATE DATABASE_DEFAULT AS tableName,
       CAST('CHECK' AS NVARCHAR(20)) COLLATE DATABASE_DEFAULT AS ruleType,
       CAST(name AS NVARCHAR(128)) COLLATE DATABASE_DEFAULT AS ruleName,
       CAST(definition AS NVARCHAR(400)) COLLATE DATABASE_DEFAULT AS detail
FROM sys.check_constraints
UNION ALL
SELECT CAST(OBJECT_NAME(parent_object_id) AS NVARCHAR(128)) COLLATE DATABASE_DEFAULT,
       CAST('UNIQUE' AS NVARCHAR(20)) COLLATE DATABASE_DEFAULT,
       CAST(name AS NVARCHAR(128)) COLLATE DATABASE_DEFAULT,
       CAST(NULL AS NVARCHAR(400))
FROM sys.key_constraints WHERE type = 'UQ'
UNION ALL
SELECT CAST(OBJECT_NAME(parent_object_id) AS NVARCHAR(128)) COLLATE DATABASE_DEFAULT,
       CAST('FOREIGN KEY' AS NVARCHAR(20)) COLLATE DATABASE_DEFAULT,
       CAST(name AS NVARCHAR(128)) COLLATE DATABASE_DEFAULT,
       CAST('ON DELETE ' + delete_referential_action_desc AS NVARCHAR(400)) COLLATE DATABASE_DEFAULT
FROM sys.foreign_keys
ORDER BY tableName, ruleType, ruleName;
GO
