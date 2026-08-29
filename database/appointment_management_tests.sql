-- ============================================================
-- SUNRISE DENTAL CLINIC
-- Appointment Database Verification Queries
-- ============================================================

USE sunrise_dental_db;


-- ------------------------------------------------------------
-- TEST 1: Check Appointment Table
-- ------------------------------------------------------------

SHOW CREATE TABLE appointments;


-- ------------------------------------------------------------
-- TEST 2: Inspect supporting entities
-- ------------------------------------------------------------

SHOW COLUMNS FROM patients;
SHOW COLUMNS FROM dentists;
SHOW COLUMNS FROM treatments;


-- ------------------------------------------------------------
-- TEST 3: Appointment Details View
-- ------------------------------------------------------------

SELECT *
FROM vw_appointment_details
ORDER BY appointment_date, appointment_time;


-- ------------------------------------------------------------
-- TEST 4: Stored Function
-- ------------------------------------------------------------

SELECT
    treatment_code,
    treatment_name,
    treatment_price,
    consultation_fee,
    fn_calculate_treatment_total(
        treatment_price,
        consultation_fee
    ) AS calculated_total
FROM treatments
ORDER BY treatment_id;


-- ------------------------------------------------------------
-- TEST 5: Daily Schedule Stored Procedure
-- ------------------------------------------------------------

CALL sp_get_daily_appointment_schedule('2026-09-01');


-- ------------------------------------------------------------
-- TEST 6: Memento Appointment History
-- ------------------------------------------------------------

SELECT
    history_id,
    appointment_id,
    appointment_number,
    patient_id,
    dentist_id,
    treatment_id,
    appointment_date,
    appointment_time,
    status,
    notes,
    changed_by,
    change_type,
    changed_at
FROM appointment_history
ORDER BY history_id;


-- ------------------------------------------------------------
-- TEST 7: Trigger Protection
-- IMPORTANT:
-- This query SHOULD FAIL with MySQL error #1644.
-- ------------------------------------------------------------

-- DELETE FROM appointments
-- WHERE appointment_id = 4;


-- ------------------------------------------------------------
-- TEST 8: Verify protected appointment remains
-- ------------------------------------------------------------

SELECT
    appointment_id,
    appointment_number,
    appointment_date,
    appointment_time,
    status
FROM appointments
WHERE appointment_id = 4;