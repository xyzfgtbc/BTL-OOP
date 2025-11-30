-- =========================================================================
-- SCRIPT TAO DATABASE CHO UNG DUNG QUAN LY BAN HANG
-- DATABASE: convenience_store
-- =========================================================================

-- Tao Database neu chua ton tai
CREATE DATABASE IF NOT EXISTS convenience_store;
USE convenience_store;

-- 1. Bảng Products (Sản phẩm tồn kho)
CREATE TABLE IF NOT EXISTS products (
    product_id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    base_price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL,
    type VARCHAR(50) NOT NULL, -- 'FOOD' hoac 'DRINK'
    extra_info VARCHAR(255) -- Thong tin bo sung: Ngay het han (YYYY-MM-DD) hoac Dung tich (Liters)
);

-- 2. Bảng Customers (Khách hàng và Điểm tích lũy)
CREATE TABLE IF NOT EXISTS customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(15) UNIQUE NOT NULL,
    loyalty_points INT DEFAULT 0
);

-- 3. Bảng Employees (Nhân viên)
CREATE TABLE IF NOT EXISTS employees (
    employee_id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    position VARCHAR(50) NOT NULL, -- Vi tri (VD: "Quan ly", "Thu ngan")
    salary DECIMAL(10, 2) NOT NULL
);

-- 4. Bảng Transactions (Giao dịch/Hóa đơn)
CREATE TABLE IF NOT EXISTS transactions (
    transaction_id VARCHAR(20) PRIMARY KEY,
    employee_id VARCHAR(10) NOT NULL,
    customer_id INT, 
    transaction_date DATETIME NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    -- Khóa ngoại liên kết với bảng customers (co the la NULL neu khach vang lai)
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    -- Khóa ngoại liên kết với bảng employees
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
);

-- 5. Bảng Transaction Details (Chi tiết từng mặt hàng trong hóa đơn)
CREATE TABLE IF NOT EXISTS transaction_details (
    detail_id INT AUTO_INCREMENT PRIMARY KEY,
    transaction_id VARCHAR(20) NOT NULL,
    product_id VARCHAR(10) NOT NULL,
    sold_quantity INT NOT NULL,
    sold_price DECIMAL(10, 2) NOT NULL, 
    -- Khóa ngoại liên kết với transactions
    FOREIGN KEY (transaction_id) REFERENCES transactions(transaction_id),
    -- Khóa ngoại liên kết với products
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);
