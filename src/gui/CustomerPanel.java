package com.cstore.gui;

import com.cstore.data.DBManager;
import com.cstore.model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

/** Panel hien thi va quan ly khach hang. */
public class CustomerPanel extends JPanel {
    private DBManager dbManager;
    private JFrame parentFrame;
    private DefaultTableModel customerTableModel;

    public CustomerPanel(DBManager dbManager, JFrame parentFrame) {
        this.dbManager = dbManager;
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout(10, 10));
        setupCustomerTable();
        setupFunctionButtons();
        refreshCustomerTable();
    }

    private void setupCustomerTable() {
        Vector<String> columnNames = new Vector<>();
        columnNames.add("ID"); columnNames.add("Ten Khach Hang"); columnNames.add("SDT"); columnNames.add("Diem Tich Luy");

        customerTableModel = new DefaultTableModel(columnNames, 0);
        JTable customerTable = new JTable(customerTableModel);
        customerTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        customerTable.setRowHeight(25);
        customerTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        customerTable.setAutoCreateRowSorter(true); 

        add(new JScrollPane(customerTable), BorderLayout.CENTER);
    }
    
    private void setupFunctionButtons() {
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnAdd = new JButton("Them Khach Hang"); JButton btnUpdate = new JButton("Cap Nhat Khach Hang");
        JButton btnDelete = new JButton("Xoa Khach Hang"); JButton btnRefresh = new JButton("Lam Moi");
        
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14)); btnUpdate.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 14)); btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 14));

        btnAdd.addActionListener(e -> showCustomerDialog(null)); btnUpdate.addActionListener(e -> handleUpdateCustomer());
        btnDelete.addActionListener(e -> handleDeleteCustomer()); btnRefresh.addActionListener(e -> refreshCustomerTable());

        btnPanel.add(btnAdd); btnPanel.add(btnUpdate); btnPanel.add(btnDelete); btnPanel.add(btnRefresh);
        add(btnPanel, BorderLayout.SOUTH);
    }

    public void refreshCustomerTable() {
        try {
            List<Customer> customers = dbManager.loadAllCustomers();
            customerTableModel.setRowCount(0); 
            for (Customer c : customers) { customerTableModel.addRow(c.toRowData()); }
            customerTableModel.fireTableDataChanged();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parentFrame, "Loi khi tai du lieu khach hang tu DB: " + e.getMessage(), "Loi DB", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleUpdateCustomer() {
        // Lay thong tin khach hang da chon
    }
    
    private void handleDeleteCustomer() {
        // Xu ly xoa khach hang
    }

    private void showCustomerDialog(Customer customerToEdit) {
        // Xu ly hien thi dialog them/cap nhat khach hang
    }
}
