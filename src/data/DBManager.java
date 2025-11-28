package com.cstore.data;

import com.cstore.model.*;

import javax.swing.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Lop DBManager - Quan ly ket noi va thao tac voi MySQL Database bang JDBC.
 */
public class DBManager {

    // THAY DOI THONG TIN KET NOI O DAY!
    private static final String DB_URL = "jdbc:mysql://localhost:3306/convenience_store";
    private static final String USER = "root";
    private static final String PASS = ""; 

    public DBManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Khong tim thay JDBC Driver cho MySQL. Vui long them Connector/J JAR vao Classpath.", "Loi Driver", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }
    
    // Thao tac Products
  
    public List<Product> loadAllProducts() throws SQLException {
        List<Product> inventory = new ArrayList<>();
        String sql = "SELECT * FROM products";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String id = rs.getString("product_id"); String name = rs.getString("name"); double basePrice = rs.getDouble("base_price");
                int quantity = rs.getInt("quantity"); String type = rs.getString("type"); String extraInfo = rs.getString("extra_info");
                if ("FOOD".equalsIgnoreCase(type)) { inventory.add(new FoodProduct(id, name, basePrice, quantity, extraInfo));
                } else if ("DRINK".equalsIgnoreCase(type)) { double volume = 0; try { volume = Double.parseDouble(extraInfo); } catch (NumberFormatException ignored) {}
                    inventory.add(new DrinkProduct(id, name, basePrice, quantity, volume));
                }
            }
        }
        return inventory;
    }

    public void addProduct(Product product) throws SQLException {
        String sql = "INSERT INTO products (product_id, name, base_price, quantity, type, extra_info) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, product.getId()); pstmt.setString(2, product.getName()); pstmt.setDouble(3, product.getBasePrice());
            pstmt.setInt(4, product.getQuantity());
            String type; String extraInfo;
            if (product instanceof FoodProduct) { type = "FOOD"; extraInfo = ((FoodProduct) product).getExtraInfo();
            } else if (product instanceof DrinkProduct) { type = "DRINK"; extraInfo = ((DrinkProduct) product).getExtraInfo();
            } else { type = "OTHER"; extraInfo = ""; }
            pstmt.setString(5, type); pstmt.setString(6, extraInfo);
            pstmt.executeUpdate();
        }
    }
    
    public void updateProductQuantity(String id, int newQuantity) throws SQLException {
        String sql = "UPDATE products SET quantity = ? WHERE product_id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newQuantity); pstmt.setString(2, id);
            pstmt.executeUpdate();
        }
    }

    // Thao tac Customers
    
    public List<Customer> loadAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("customer_id"); String name = rs.getString("name"); String phone = rs.getString("phone_number");
                int points = rs.getInt("loyalty_points"); customers.add(new Customer(id, name, phone, points));
            }
        }
        return customers;
    }
    
    public Customer findCustomerByPhone(String phone) throws SQLException {
        String sql = "SELECT * FROM customers WHERE phone_number = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, phone);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("customer_id"); String name = rs.getString("name"); String phoneNum = rs.getString("phone_number");
                    int points = rs.getInt("loyalty_points"); return new Customer(id, name, phoneNum, points);
                }
            }
        }
        return null;
    }

    public void addCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO customers (name, phone_number, loyalty_points) VALUES (?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customer.getName()); pstmt.setString(2, customer.getPhoneNumber());
            pstmt.setInt(3, customer.getLoyaltyPoints()); pstmt.executeUpdate();
        }
    }

    public void updateCustomer(Customer customer) throws SQLException {
        String sql = "UPDATE customers SET name = ?, phone_number = ?, loyalty_points = ? WHERE customer_id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customer.getName()); pstmt.setString(2, customer.getPhoneNumber());
            pstmt.setInt(3, customer.getLoyaltyPoints()); pstmt.setInt(4, customer.getId());
            pstmt.executeUpdate();
        }
    }
    
    public void deleteCustomer(int customerId) throws SQLException {
        String sql = "DELETE FROM customers WHERE customer_id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId); pstmt.executeUpdate();
        }
    }
    
    // Thao tac Employees
  
    public List<Employee> loadAllEmployees() throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employees";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String id = rs.getString("employee_id"); String name = rs.getString("name");
                String position = rs.getString("position"); double salary = rs.getDouble("salary");
                employees.add(new Employee(id, name, position, salary));
            }
        }
        return employees;
    }
    
    public void addEmployee(Employee employee) throws SQLException {
        String sql = "INSERT INTO employees (employee_id, name, position, salary) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, employee.getId()); pstmt.setString(2, employee.getName());
            pstmt.setString(3, employee.getPosition()); pstmt.setDouble(4, employee.getSalary());
            pstmt.executeUpdate();
        }
    }


    // Thao tac Transactions (Bao gom Tich diem)

    public void saveTransaction(SalesTransaction transaction, List<Product> soldItems, Integer customerId) throws SQLException {
        Connection conn = getConnection(); conn.setAutoCommit(false); 
        
        try {
            // 1. Luu vao bang transactions
            String sqlTrans = "INSERT INTO transactions (transaction_id, employee_id, customer_id, transaction_date, total_amount) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlTrans)) {
                pstmt.setString(1, transaction.getTransactionId()); pstmt.setString(2, transaction.getEmployeeId());
                if (customerId != null) { pstmt.setInt(3, customerId); } else { pstmt.setNull(3, Types.INTEGER); }
                pstmt.setTimestamp(4, Timestamp.valueOf(transaction.getTransactionDate()));
                pstmt.setDouble(5, transaction.getTotalAmount()); pstmt.executeUpdate();
            }
            
            // 2. Luu vao bang transaction_details
            String sqlDetail = "INSERT INTO transaction_details (transaction_id, product_id, sold_quantity, sold_price) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlDetail)) {
                for (Product item : soldItems) {
                    pstmt.setString(1, transaction.getTransactionId()); pstmt.setString(2, item.getId());
                    pstmt.setInt(3, item.getQuantity()); pstmt.setDouble(4, item.calculatePrice()); 
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
            
            // 3. Tich diem cho khach hang (1 diem / 10,000 VND)
            if (customerId != null) {
                int pointsEarned = (int) (transaction.getTotalAmount() / 10000); 
                String sqlPoints = "UPDATE customers SET loyalty_points = loyalty_points + ? WHERE customer_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sqlPoints)) {
                    pstmt.setInt(1, pointsEarned); pstmt.setInt(2, customerId); pstmt.executeUpdate();
                }
            }

            conn.commit(); 
        } catch (SQLException e) {
            conn.rollback(); 
            throw e;
        } finally {
            conn.setAutoCommit(true); conn.close();
        }
    }
    
    public List<SalesTransaction> loadAllTransactions() throws SQLException {
        List<SalesTransaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY transaction_date DESC";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String id = rs.getString("transaction_id"); String employeeId = rs.getString("employee_id");
                Integer customerId = rs.getObject("customer_id", Integer.class); 
                LocalDateTime date = rs.getTimestamp("transaction_date").toLocalDateTime();
                double total = rs.getDouble("total_amount");
                transactions.add(new SalesTransaction(id, employeeId, customerId, date, total));
            }
        }
        return transactions;
    }

    public void insertSampleData(boolean force) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM products";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(checkSql)) {
            if (force || (rs.next() && rs.getInt(1) == 0)) {
                // Products
                addProduct(new FoodProduct("F001", "Banh mi sandwich", 15000, 50, "2025-12-30"));
                addProduct(new FoodProduct("F002", "Mi an lien Hao Hao", 10000, 100, "2026-06-01"));
                addProduct(new DrinkProduct("D001", "Nuoc ngot Coca 330ml", 12000, 80, 0.33));
                addProduct(new DrinkProduct("D002", "Tra sua dong chai 500ml", 25000, 30, 0.5));
                
                // Employees
                addEmployee(new Employee("NV001", "Le Thi Thuy", "Thu ngan", 7500000));
                addEmployee(new Employee("NV002", "Tran Van Nam", "Quan ly", 12000000));

                // Customers
                addCustomer(new Customer("Nguyen Van A", "0901112222"));
                addCustomer(new Customer( "Tran Thi B", "0903334444"));
                
                JOptionPane.showMessageDialog(null, "Da chen du lieu mau vao Database.", "Khoi tao Data", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}
