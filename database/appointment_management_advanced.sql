


-- ============================================================
-- SUNRISE DENTAL CLINIC MANAGEMENT SYSTEM
-- Appointment Management - Advanced Database Objects
-- ============================================================

USE sunrise_dental_db;

-- ============================================================
-- 1. SCHEMA CONSISTENCY
-- ============================================================

-- AppointmentService accepts notes up to 1000 characters.
ALTER TABLE appointments
MODIFY COLUMN notes VARCHAR(1000) NULL;

-- Appointment Memento/history snapshots may also contain
-- appointment notes up to 1000 characters.
ALTER TABLE appointment_history
MODIFY COLUMN notes VARCHAR(1000) NULL;


-- ============================================================
-- 2. STORED FUNCTION
-- ============================================================
-- Calculates expected treatment cost:
-- Treatment Price + Consultation Fee
-- ============================================================

DROP FUNCTION IF EXISTS fn_calculate_treatment_total;

CREATE FUNCTION fn_calculate_treatment_total(
    p_treatment_price DECIMAL(10,2),
    p_consultation_fee DECIMAL(10,2)
)
RETURNS DECIMAL(10,2)
DETERMINISTIC
RETURN
    COALESCE(p_treatment_price, 0.00)
    +
    COALESCE(p_consultation_fee, 0.00);


-- ============================================================
-- 3. APPOINTMENT DETAILS VIEW
-- ============================================================
-- Combines appointment, patient, dentist and treatment
-- information for search, reporting and appointment details.
-- ============================================================

CREATE OR REPLACE VIEW vw_appointment_details AS

SELECT
    -- Appointment information
    a.appointment_id,
    a.appointment_number,
    a.appointment_date,
    a.appointment_time,
    a.status,
    a.notes,

    -- Patient information
    p.patient_id,
    p.patient_code,
    p.full_name AS patient_name,
    p.address AS patient_address,
    p.contact_number AS patient_contact_number,
    p.email AS patient_email,

    -- Dentist information
    d.dentist_id,
    d.dentist_code,
    d.full_name AS dentist_name,
    d.specialization AS dentist_specialization,
    d.contact_number AS dentist_contact_number,
    d.email AS dentist_email,
    d.is_active AS dentist_active,

    -- Treatment information
    t.treatment_id,
    t.treatment_code,
    t.treatment_name,
    t.description AS treatment_description,
    t.treatment_price,
    t.consultation_fee,
    t.estimated_duration_minutes,
    t.is_active AS treatment_active,

    -- Estimated appointment end time
    ADDTIME(
        a.appointment_time,
        SEC_TO_TIME(
            COALESCE(t.estimated_duration_minutes, 0) * 60
        )
    ) AS estimated_end_time,

    -- Reusable stored function used for cost calculation
    fn_calculate_treatment_total(
        t.treatment_price,
        t.consultation_fee
    ) AS estimated_total_cost,

    -- Audit information
    a.created_by,
    a.created_at,
    a.updated_at

FROM appointments a

INNER JOIN patients p
    ON a.patient_id = p.patient_id

INNER JOIN dentists d
    ON a.dentist_id = d.dentist_id

INNER JOIN treatments t
    ON a.treatment_id = t.treatment_id;


-- ============================================================
-- 4. DAILY APPOINTMENT SCHEDULE STORED PROCEDURE
-- ============================================================
-- Returns a complete schedule for a selected clinic date.
-- Uses vw_appointment_details rather than repeating joins.
-- ============================================================

DROP PROCEDURE IF EXISTS sp_get_daily_appointment_schedule;

CREATE PROCEDURE sp_get_daily_appointment_schedule(
    IN p_appointment_date DATE
)
SELECT
    appointment_id,
    appointment_number,
    appointment_date,
    appointment_time,
    estimated_end_time,
    status,

    patient_code,
    patient_name,
    patient_contact_number,

    dentist_code,
    dentist_name,
    dentist_specialization,

    treatment_code,
    treatment_name,
    estimated_duration_minutes,

    treatment_price,
    consultation_fee,
    estimated_total_cost,

    notes

FROM vw_appointment_details

WHERE appointment_date = p_appointment_date

ORDER BY
    appointment_time,
    dentist_name;


-- ============================================================
-- 5. CLINICAL RECORD PROTECTION TRIGGER
-- ============================================================
-- Physical deletion of appointment records is prohibited.
-- Appointments must instead be logically cancelled.
-- ============================================================

DROP TRIGGER IF EXISTS trg_prevent_appointment_delete;

CREATE TRIGGER trg_prevent_appointment_delete
BEFORE DELETE ON appointments
FOR EACH ROW
SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT =
    'Appointments cannot be physically deleted. Cancel the appointment instead.';


-- ============================================================
-- END OF APPOINTMENT ADVANCED DATABASE SCRIPT
-- ============================================================