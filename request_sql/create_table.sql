-- 选择数据库
USE covoiturage_full_stack;
-- 删除已存在的表（按依赖顺序）
DROP TABLE IF EXISTS reports;
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS trips;
DROP TABLE IF EXISTS users;

-- ============================================
-- 用户表 (users)
-- ============================================
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    photo_url VARCHAR(500),
    bio TEXT,
    is_verified BOOLEAN DEFAULT FALSE,
    average_rating_driver DECIMAL(3,2) DEFAULT 0.00,
    average_rating_passenger DECIMAL(3,2) DEFAULT 0.00,
    total_trips_driver INTEGER DEFAULT 0,
    total_trips_passenger INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_email (email),
    INDEX idx_is_verified (is_verified)
);

-- ============================================
-- 行程表 (trips)
-- ============================================
CREATE TABLE trips (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    driver_id INTEGER NOT NULL,
    departure_address TEXT NOT NULL,
    departure_city VARCHAR(100) NOT NULL,
    departure_lat DECIMAL(10,8),
    departure_lng DECIMAL(11,8),
    arrival_address TEXT NOT NULL,
    arrival_city VARCHAR(100) NOT NULL,
    arrival_lat DECIMAL(10,8),
    arrival_lng DECIMAL(11,8),
    departure_datetime TIMESTAMP NOT NULL,
    total_seats INTEGER NOT NULL,
    available_seats INTEGER NOT NULL,
    price_per_seat DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'active',
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (driver_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_driver (driver_id),
    INDEX idx_departure_city (departure_city),
    INDEX idx_arrival_city (arrival_city),
    INDEX idx_departure_datetime (departure_datetime),
    INDEX idx_status (status)
);

-- ============================================
-- 预订表 (bookings)
-- ============================================
CREATE TABLE bookings (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    trip_id INTEGER NOT NULL,
    passenger_id INTEGER NOT NULL,
    seats_booked INTEGER NOT NULL DEFAULT 1,
    total_price DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'pending',
    message_to_driver TEXT,
    payment_status VARCHAR(20) DEFAULT 'unpaid',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE,
    FOREIGN KEY (passenger_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_trip (trip_id),
    INDEX idx_passenger (passenger_id),
    INDEX idx_status (status),
    INDEX idx_payment_status (payment_status)
);

-- ============================================
-- 评价表 (reviews)
-- ============================================
CREATE TABLE reviews (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    booking_id INTEGER NOT NULL,
    reviewer_id INTEGER NOT NULL,
    reviewee_id INTEGER NOT NULL,
    rating INTEGER NOT NULL,
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    FOREIGN KEY (reviewer_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (reviewee_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_booking (booking_id),
    INDEX idx_reviewer (reviewer_id),
    INDEX idx_reviewee (reviewee_id),
    INDEX idx_rating (rating),
    
    CHECK (rating >= 1 AND rating <= 5)
);

-- ============================================
-- 消息表 (messages)
-- ============================================
CREATE TABLE messages (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    booking_id INTEGER NOT NULL,
    sender_id INTEGER NOT NULL,
    receiver_id INTEGER NOT NULL,
    content TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_booking (booking_id),
    INDEX idx_sender (sender_id),
    INDEX idx_receiver (receiver_id),
    INDEX idx_is_read (is_read)
);

-- ============================================
-- 通知表 (notifications)
-- ============================================
CREATE TABLE notifications (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    user_id INTEGER NOT NULL,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user (user_id),
    INDEX idx_type (type),
    INDEX idx_is_read (is_read),
    INDEX idx_created_at (created_at)
);

-- ============================================
-- 举报表 (reports)
-- ============================================
CREATE TABLE reports (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    reporter_id INTEGER NOT NULL,
    reported_user_id INTEGER,
    report_type VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(20) DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (reporter_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (reported_user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_reporter (reporter_id),
    INDEX idx_reported_user (reported_user_id),
    INDEX idx_status (status),
    INDEX idx_report_type (report_type)
);