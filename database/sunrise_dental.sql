

CREATE DATABASE IF NOT EXISTS sunrise_dental_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE sunrise_dental_db;


-- TABLE: users


CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role ENUM('ADMIN', 'RECEPTIONIST') NOT NULL DEFAULT 'RECEPTIONIST',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_users_username UNIQUE (username)
);



-- =====================================================
-- TABLE: patients
-- Stores patient personal and contact information


CREATE TABLE patients (
    patient_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_code VARCHAR(20) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    contact_number VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    date_of_birth DATE,
    gender ENUM('MALE', 'FEMALE', 'OTHER'),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uq_patients_patient_code UNIQUE (patient_code)
);




-- TABLE: dentists
-- Stores dentist information


CREATE TABLE dentists (
    dentist_id INT AUTO_INCREMENT PRIMARY KEY,
    dentist_code VARCHAR(20) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100),
    contact_number VARCHAR(20),
    email VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_dentists_dentist_code UNIQUE (dentist_code)
);




-- TABLE: treatments
-- Stores dental treatment types and pricing


CREATE TABLE treatments (
    treatment_id INT AUTO_INCREMENT PRIMARY KEY,
    treatment_code VARCHAR(20) NOT NULL,
    treatment_name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    treatment_price DECIMAL(10,2) NOT NULL,
    consultation_fee DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    estimated_duration_minutes INT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_treatments_treatment_code UNIQUE (treatment_code),
    CONSTRAINT chk_treatment_price CHECK (treatment_price >= 0),
    CONSTRAINT chk_consultation_fee CHECK (consultation_fee >= 0)
);



-- TABLE: appointments
-- Stores patient appointment information


CREATE TABLE appointments (
    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
    appointment_number VARCHAR(30) NOT NULL,

    patient_id INT NOT NULL,
    dentist_id INT NOT NULL,
    treatment_id INT NOT NULL,

    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,

    status ENUM('SCHEDULED', 'COMPLETED', 'CANCELLED')
        NOT NULL DEFAULT 'SCHEDULED',

    notes VARCHAR(500),

    created_by INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uq_appointments_number
        UNIQUE (appointment_number),

    CONSTRAINT fk_appointments_patient
        FOREIGN KEY (patient_id)
        REFERENCES patients(patient_id),

    CONSTRAINT fk_appointments_dentist
        FOREIGN KEY (dentist_id)
        REFERENCES dentists(dentist_id),

    CONSTRAINT fk_appointments_treatment
        FOREIGN KEY (treatment_id)
        REFERENCES treatments(treatment_id),

    CONSTRAINT fk_appointments_created_by
        FOREIGN KEY (created_by)
        REFERENCES users(user_id),


);




-- TABLE: bills
-- Stores billing summary for completed appointments


CREATE TABLE bills (
    bill_id INT AUTO_INCREMENT PRIMARY KEY,
    bill_number VARCHAR(30) NOT NULL,

    appointment_id INT NOT NULL,

    subtotal DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    additional_charges DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    discount_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,

    payment_status ENUM('UNPAID', 'PAID', 'PARTIAL')
        NOT NULL DEFAULT 'UNPAID',

    generated_by INT NOT NULL,
    generated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_bills_bill_number
        UNIQUE (bill_number),

    CONSTRAINT uq_bills_appointment
        UNIQUE (appointment_id),

    CONSTRAINT fk_bills_appointment
        FOREIGN KEY (appointment_id)
        REFERENCES appointments(appointment_id),

    CONSTRAINT fk_bills_generated_by
        FOREIGN KEY (generated_by)
        REFERENCES users(user_id),

    CONSTRAINT chk_bills_subtotal
        CHECK (subtotal >= 0),

    CONSTRAINT chk_bills_additional
        CHECK (additional_charges >= 0),

    CONSTRAINT chk_bills_discount
        CHECK (discount_amount >= 0),

    CONSTRAINT chk_bills_total
        CHECK (total_amount >= 0)
);




-- TABLE: bill_items
-- Stores individual bill line items

CREATE TABLE bill_items (
    bill_item_id INT AUTO_INCREMENT PRIMARY KEY,

    bill_id INT NOT NULL,

    item_name VARCHAR(150) NOT NULL,
    item_type ENUM('CONSULTATION', 'TREATMENT', 'EXTRA')
        NOT NULL,

    quantity INT NOT NULL DEFAULT 1,
    unit_price DECIMAL(10,2) NOT NULL,
    line_total DECIMAL(10,2) NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_bill_items_bill
        FOREIGN KEY (bill_id)
        REFERENCES bills(bill_id)
        ON DELETE CASCADE,

    CONSTRAINT chk_bill_items_quantity
        CHECK (quantity > 0),

    CONSTRAINT chk_bill_items_unit_price
        CHECK (unit_price >= 0),

    CONSTRAINT chk_bill_items_line_total
        CHECK (line_total >= 0)
);




-- TABLE: appointment_history
-- Stores previous appointment states for audit/history
-- Supports the Memento design pattern


CREATE TABLE appointment_history (
    history_id INT AUTO_INCREMENT PRIMARY KEY,

    appointment_id INT NOT NULL,
    appointment_number VARCHAR(30) NOT NULL,

    patient_id INT NOT NULL,
    dentist_id INT NOT NULL,
    treatment_id INT NOT NULL,

    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,

    status ENUM('SCHEDULED', 'COMPLETED', 'CANCELLED')
        NOT NULL,

    notes VARCHAR(1000),

    changed_by INT NOT NULL,
    change_type ENUM('UPDATE', 'CANCEL')
        NOT NULL,

    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_history_appointment
        FOREIGN KEY (appointment_id)
        REFERENCES appointments(appointment_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_history_patient
        FOREIGN KEY (patient_id)
        REFERENCES patients(patient_id),

    CONSTRAINT fk_history_dentist
        FOREIGN KEY (dentist_id)
        REFERENCES dentists(dentist_id),

    CONSTRAINT fk_history_treatment
        FOREIGN KEY (treatment_id)
        REFERENCES treatments(treatment_id),

    CONSTRAINT fk_history_changed_by
        FOREIGN KEY (changed_by)
        REFERENCES users(user_id)
);



-- TABLE: audit_logs
-- Records important system actions


CREATE TABLE audit_logs (
    audit_id INT AUTO_INCREMENT PRIMARY KEY,

    user_id INT,
    action_type VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50),
    entity_id INT,

    description VARCHAR(500),

    ip_address VARCHAR(45),

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_audit_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE SET NULL
);





-- INDEXES
-- Improve common search and reporting performance


CREATE INDEX idx_patients_full_name
ON patients(full_name);

CREATE INDEX idx_patients_contact
ON patients(contact_number);

CREATE INDEX idx_dentists_full_name
ON dentists(full_name);

CREATE INDEX idx_treatments_name
ON treatments(treatment_name);

CREATE INDEX idx_appointments_date
ON appointments(appointment_date);

CREATE INDEX idx_appointments_patient
ON appointments(patient_id);

CREATE INDEX idx_appointments_status_date
ON appointments(status, appointment_date);

CREATE INDEX idx_bills_generated_at
ON bills(generated_at);




-- INITIAL TREATMENT DATA


INSERT INTO treatments
(treatment_code, treatment_name, description, treatment_price, consultation_fee, estimated_duration_minutes)
VALUES
('TRT-001', 'Dental Consultation',
 'General dental examination and consultation',
 0.00, 1500.00, 30),

('TRT-002', 'Dental Cleaning',
 'Professional teeth cleaning and scaling',
 3500.00, 1500.00, 45),

('TRT-003', 'Tooth Filling',
 'Dental filling treatment for cavities',
 5000.00, 1500.00, 45),

('TRT-004', 'Tooth Extraction',
 'Standard tooth extraction procedure',
 7500.00, 1500.00, 60),

('TRT-005', 'Root Canal Treatment',
 'Root canal treatment procedure',
 18000.00, 1500.00, 90),

('TRT-006', 'Teeth Whitening',
 'Professional teeth whitening treatment',
 12000.00, 1500.00, 60);




-- INITIAL DENTIST DATA
-- Sample data used for system development and testing


INSERT INTO dentists
(dentist_code, full_name, specialization, contact_number, email)
VALUES
('DEN-001', 'Dr. Nimal Perera', 'General Dentistry', '0771234567', 'nimal.perera@sunrisedental.lk'),

('DEN-002', 'Dr. Anjali Fernando', 'Orthodontics', '0772345678', 'anjali.fernando@sunrisedental.lk'),

('DEN-003', 'Dr. Kavindu Silva', 'Endodontics', '0773456789', 'kavindu.silva@sunrisedental.lk'),

('DEN-004', 'Dr. Shalini Jayawardena', 'Cosmetic Dentistry', '0774567890', 'shalini.j@sunrisedental.lk');





-- INITIAL SYSTEM USERS
-- Passwords stored as SHA-256 hashes, not plain text


INSERT INTO users
(username, password_hash, full_name, role)
VALUES
(
    'admin',
    '210000:Dms2vyta8HHAjddTW20mYw==:DEq5J3erz+LhBPJM6pyCzzLbKLRjyuvGNVFOxqv6Lx0=',
    'System Administrator',
    'ADMIN'
),
(
    'reception',
    '210000:jOXJw4hAi9KH5Hx322J/+A==:+gj16z2hZpRKx+ciLo9NcB1hloC4tRSQI221iB0WcJ4=',
    'Reception Staff',
    'RECEPTIONIST'
);





-- SAMPLE PATIENT DATA
-- Used for development and database testing


INSERT INTO patients
(patient_code, full_name, address, contact_number, email, date_of_birth, gender)
VALUES
(
    'PAT-0001',
    'Kamal Fernando',
    'Colombo 05, Sri Lanka',
    '0712345678',
    'kamal.fernando@example.com',
    '1995-06-15',
    'MALE'
);





-- SAMPLE VALID APPOINTMENT
-- Used to verify foreign-key relationships


INSERT INTO appointments
(
    appointment_number,
    patient_id,
    dentist_id,
    treatment_id,
    appointment_date,
    appointment_time,
    status,
    notes,
    created_by
)
VALUES
(
    'APT-0001',

    (SELECT patient_id
     FROM patients
     WHERE patient_code = 'PAT-0001'),

    (SELECT dentist_id
     FROM dentists
     WHERE dentist_code = 'DEN-001'),

    (SELECT treatment_id
     FROM treatments
     WHERE treatment_code = 'TRT-002'),

    '2026-09-01',
    '10:00:00',
    'SCHEDULED',
    'Initial dental cleaning appointment',

    (SELECT user_id
     FROM users
     WHERE username = 'reception')
);








-- Ensure billing tables support transactions and rollback
ALTER TABLE bills ENGINE = InnoDB;
ALTER TABLE bill_items ENGINE = InnoDB;












