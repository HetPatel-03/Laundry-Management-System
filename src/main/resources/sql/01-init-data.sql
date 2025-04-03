-- Initialize database with default data

-- Admin user
INSERT INTO users (name, email, phone_number, password, role, address, active, created_at, updated_at) 
VALUES ('Admin User', 'admin@laundry.com', '1234567890', '$2a$10$BKSUEUKbUInnNx7c70gM4OYXQw9XIk1uB4YJIQXcBZG.4jRyKQlOa', 'ADMIN', '123 Admin St, City', 1, NOW(), NOW());

-- Staff users
INSERT INTO users (name, email, phone_number, password, role, address, active, created_at, updated_at) 
VALUES ('Staff One', 'staff1@laundry.com', '2345678901', '$2a$10$BKSUEUKbUInnNx7c70gM4OYXQw9XIk1uB4YJIQXcBZG.4jRyKQlOa', 'STAFF', '456 Staff St, City', 1, NOW(), NOW());

INSERT INTO users (name, email, phone_number, password, role, address, active, created_at, updated_at) 
VALUES ('Staff Two', 'staff2@laundry.com', '3456789012', '$2a$10$BKSUEUKbUInnNx7c70gM4OYXQw9XIk1uB4YJIQXcBZG.4jRyKQlOa', 'STAFF', '789 Staff Ave, City', 1, NOW(), NOW());

-- Customer users
INSERT INTO users (name, email, phone_number, password, role, address, active, created_at, updated_at) 
VALUES ('John Doe', 'john@example.com', '4567890123', '$2a$10$BKSUEUKbUInnNx7c70gM4OYXQw9XIk1uB4YJIQXcBZG.4jRyKQlOa', 'CUSTOMER', '101 Customer Blvd, City', 1, NOW(), NOW());

INSERT INTO users (name, email, phone_number, password, role, address, active, created_at, updated_at) 
VALUES ('Jane Smith', 'jane@example.com', '5678901234', '$2a$10$BKSUEUKbUInnNx7c70gM4OYXQw9XIk1uB4YJIQXcBZG.4jRyKQlOa', 'CUSTOMER', '202 Client St, City', 1, NOW(), NOW());

-- Service categories
INSERT INTO service_categories (name, description, base_price, price_per_kg, estimated_time_in_hours, active, created_at, updated_at) 
VALUES ('Wash & Fold', 'Standard washing and folding service', 10.00, 2.50, 24, 1, NOW(), NOW());

INSERT INTO service_categories (name, description, base_price, price_per_kg, estimated_time_in_hours, active, created_at, updated_at) 
VALUES ('Dry Cleaning', 'Professional dry cleaning for delicate items', 15.00, 5.00, 48, 1, NOW(), NOW());

INSERT INTO service_categories (name, description, base_price, price_per_kg, estimated_time_in_hours, active, created_at, updated_at) 
VALUES ('Wash & Iron', 'Washing with professional ironing service', 12.50, 3.50, 36, 1, NOW(), NOW());

INSERT INTO service_categories (name, description, base_price, price_per_kg, estimated_time_in_hours, active, created_at, updated_at) 
VALUES ('Premium Care', 'Premium service with specialized treatment for luxury items', 25.00, 7.50, 72, 1, NOW(), NOW());

-- The password for all users is 'password123'