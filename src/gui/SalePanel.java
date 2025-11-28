package com.cstore.gui;

import com.cstore.data.DBManager;
import com.cstore.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel Tạo Giao Dịch Bán Hàng (POS - Point of Sale).
 */
public class SalePanel extends JPanel {
    private DBManager dbManager;
    private JFrame parentFrame;
    private InventoryPanel inventoryPanel;
    private CustomerPanel customerPanel;

    // Thành phần UI
    private DefaultTableModel saleTableModel;
    private JTable saleTable;
    private JLabel lblTotalAmount;
    private JLabel lblCustomerInfo;
    private JTextField txtProductId;
    private JTextField txtPhoneNumber;
    
    // Dữ liệu tạm thời
    private List<Product> currentSaleItems; 
    private Customer currentCustomer = null; 
    private double discountRate = 0.0; // Tỷ lệ giảm giá (%)

    public SalePanel(DBManager dbManager, JFrame parentFrame, InventoryPanel inventoryPanel, CustomerPanel customerPanel) {
        this.dbManager = dbManager;
        this.parentFrame = parentFrame;
        this.inventoryPanel = inventoryPanel;
        this.customerPanel = customerPanel;
        this.currentSaleItems = new ArrayList<>();
        
        setLayout(new BorderLayout(10, 10));
        setupSaleUI();
        resetTransaction(); 
    }
    
    private void setupSaleUI() {
        // --- Phần chính: Bảng hóa đơn ---
        saleTableModel = new DefaultTableModel(new Object[]{"ID", "Tên SP", "SL", "Đơn Giá", "Thành Tiền"}, 0);
        saleTable = new JTable(saleTableModel);
        saleTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        saleTable.setRowHeight(25);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(new JScrollPane(saleTable), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // --- Phần nhập liệu và chức năng (NORTH & EAST) ---
        add(createInputAndCustomerPanel(), BorderLayout.NORTH);
        add(createPaymentPanel(), BorderLayout.EAST);
        
        // --- Phần tổng tiền (SOUTH) ---
        add(createTotalDisplayPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createInputAndCustomerPanel() {
        JPanel northPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        
        // 1. Panel Nhập liệu (Quét mã/ID)
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        txtProductId = new JTextField(15);
        JButton btnAdd = new JButton("Thêm SP (Enter)");
        
        txtProductId.addActionListener(e -> addItemToSale());
        btnAdd.addActionListener(e -> addItemToSale());
        
        inputPanel.setBorder(BorderFactory.createTitledBorder("Nhập Mã Sản Phẩm"));
        inputPanel.add(new JLabel("Mã/ID:"));
        inputPanel.add(txtProductId);
        inputPanel.add(btnAdd);
        
        // 2. Panel Khách hàng
        JPanel customerPanel = new JPanel(new BorderLayout(5, 5));
        txtPhoneNumber = new JTextField(12);
        JButton btnFindCustomer = new JButton("Tìm KH");
        lblCustomerInfo = new JLabel("KH Vãng Lai", SwingConstants.CENTER);
        lblCustomerInfo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCustomerInfo.setForeground(Color.BLUE);
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        searchPanel.add(new JLabel("SĐT:"));
        searchPanel.add(txtPhoneNumber);
        searchPanel.add(btnFindCustomer);
        
        txtPhoneNumber.addActionListener(e -> findCustomerByPhone());
        btnFindCustomer.addActionListener(e -> findCustomerByPhone());
        
        customerPanel.
