package com.cstore.gui;

import com.cstore.data.DBManager;
import com.cstore.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

/** Panel hien thi va quan ly ton kho. */
public class InventoryPanel extends JPanel {
    private DBManager dbManager;
    private DefaultTableModel inventoryTableModel;
    private JFrame parentFrame;

    public InventoryPanel(DBManager dbManager, JFrame parentFrame) {
        this.dbManager = dbManager;
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());
        setupInventoryTable();
        setupFunctionButtons();
        refreshInventoryTable(); 
    }

    private void setupInventoryTable() {
        Vector<String> columnNames = new Vector<>();
        columnNames.add("ID"); columnNames.add("Ten San Pham"); columnNames.add("Loai");
        columnNames.add("Gia Goc (VND)"); columnNames.add("Ton Kho"); columnNames.add("Gia Ban (VND)");

        inventoryTableModel = new DefaultTableModel(columnNames, 0);
        JTable inventoryTable = new JTable(inventoryTableModel);
        inventoryTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inventoryTable.setRowHeight(25);
        inventoryTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        inventoryTable.setAutoCreateRowSorter(true); 

        add(new JScrollPane(inventoryTable), BorderLayout.CENTER);
    }

    private void setupFunctionButtons() {
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnAddProduct = new JButton("Them San Pham Moi");
        JButton btnRefresh = new JButton("Lam Moi Du Lieu");
        
        btnAddProduct.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        btnAddProduct.addActionListener(e -> showAddProductDialog());
        btnRefresh.addActionListener(e -> refreshInventoryTable());

        btnPanel.add(btnAddProduct);
        btnPanel.add(btnRefresh);
        
        add(btnPanel, BorderLayout.SOUTH);
    }

    public void refreshInventoryTable() {
        try {
            List<Product> products = dbManager.loadAllProducts();
            inventoryTableModel.setRowCount(0); 
            for (Product p : products) {
                inventoryTableModel.addRow(p.toRowData());
            }
            inventoryTableModel.fireTableDataChanged();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parentFrame, "Loi khi tai du lieu ton kho tu DB: " + e.getMessage(), "Loi DB", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showAddProductDialog() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        JTextField txtId = new JTextField(15); JTextField txtName = new JTextField(15);
        JTextField txtPrice = new JTextField(15); JTextField txtQuantity = new JTextField(15);
        String[] productTypes = {"Do An (FOOD)", "Do Uong (DRINK)"};
        JComboBox<String> typeComboBox = new JComboBox<>(productTypes);
        JLabel lblExtra = new JLabel("Ngay Het Han (YYYY-MM-DD):");
        JTextField txtExtra = new JTextField(15);
        
        typeComboBox.addActionListener(e -> {
            if (typeComboBox.getSelectedItem().toString().startsWith("Do Uong")) {
                lblExtra.setText("Dung Tich (Lit):");
            } else {
                lblExtra.setText("Ngay Het Han (YYYY-MM-DD):");
            }
        });

        panel.add(new JLabel("ID San Pham:")); panel.add(txtId); panel.add(new JLabel("Ten San Pham:")); panel.add(txtName);
        panel.add(new JLabel("Gia Goc:")); panel.add(txtPrice); panel.add(new JLabel("So Luong Ton:")); panel.add(txtQuantity);
        panel.add(new JLabel("Loai San Pham:")); panel.add(typeComboBox); panel.add(lblExtra); panel.add(txtExtra);

        int result = JOptionPane.showConfirmDialog(parentFrame, panel, "Them San Pham Moi", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String id = txtId.getText().trim(); String name = txtName.getText().trim();
                double price = Double.parseDouble(txtPrice.getText().trim()); int quantity = Integer.parseInt(txtQuantity.getText().trim());
                String extra = txtExtra.getText().trim(); String type = typeComboBox.getSelectedItem().toString();

                if (dbManager.loadAllProducts().stream().anyMatch(p -> p.getId().equalsIgnoreCase(id))) {
                    throw new IllegalArgumentException("Ma san pham da ton tai trong DB.");
                }

                Product newProduct = null;
                if (type.startsWith("Do An")) { newProduct = new FoodProduct(id, name, price, quantity, extra);
                } else if (type.startsWith("Do Uong")) { double volume = Double.parseDouble(extra); newProduct = new DrinkProduct(id, name, price, quantity, volume);
                }
                
                if (newProduct != null) {
                    dbManager.addProduct(newProduct);
                    refreshInventoryTable();
                    JOptionPane.showMessageDialog(parentFrame, "Them san pham vao DB thanh cong!", "Thong bao", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(parentFrame, "Loi dinh dang so (Gia/So luong/Dung tich)!", "Loi Input", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException | SQLException ex) {
                JOptionPane.showMessageDialog(parentFrame, "Loi: " + ex.getMessage(), "Loi Nghiep Vu/DB", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

