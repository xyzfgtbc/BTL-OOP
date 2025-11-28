package com.cstore.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Lop SalesTransaction - Quan ly giao dich ban hang (Hoa don). */
public class SalesTransaction {
    private String transactionId;
    private String employeeId;
    private Integer customerId; 
    private LocalDateTime transactionDate;
    private double totalAmount;

    public SalesTransaction(String transactionId, String employeeId, Integer customerId, LocalDateTime transactionDate, double totalAmount) {
        this.transactionId = transactionId;
        this.employeeId = employeeId;
        this.customerId = customerId;
        this.transactionDate = transactionDate;
        this.totalAmount = totalAmount;
    }

    public String getTransactionId() { return transactionId; }
    public String getEmployeeId() { return employeeId; }
    public Integer getCustomerId() { return customerId; }
    public double getTotalAmount() { return totalAmount; }
    public LocalDateTime getTransactionDate() { return transactionDate; }

    public Object[] toRowData() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");
        return new Object[]{
            transactionId, 
            employeeId, 
            customerId != null ? String.valueOf(customerId) : "Vang lai", 
            transactionDate.format(formatter), 
            String.format("%,.0f", totalAmount)
        };
    }
}