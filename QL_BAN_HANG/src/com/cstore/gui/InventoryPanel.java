package com.cstore.gui;

import com.cstore.data.DBManager;
import com.cstore.model.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class InventoryPanel extends JPanel {

    private DBManager dbManager;
    private JFrame parentFrame;
    private DefaultTableModel tableModel;
    private JTable table;

    public InventoryPanel(DBManager dbManager, JFrame parentFrame) {
        this.dbManager = dbManager;
        this.parentFrame = parentFrame;

        setLayout(new BorderLayout(10, 10));

        setupTable();
        setupButtons();
        loadTable();
    }

    private void setupTable() {
        Vector<String> cols = new Vector<>();
        cols.add("ID");
        cols.add("Tên sản phẩm");
        cols.add("Giá");
        cols.add("Số lượng");
        cols.add("Loại");
        cols.add("Thông tin thêm");

        tableModel = new DefaultTableModel(cols, 0);
        table = new JTable(tableModel);

        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void setupButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton btnAdd = new JButton("Thêm");
        JButton btnDelete = new JButton("Xóa");

        btnAdd.addActionListener(e -> addProduct());
        btnDelete.addActionListener(e -> deleteProduct());

        panel.add(btnAdd);
        panel.add(btnDelete);

        add(panel, BorderLayout.SOUTH);
    }

    public void loadTable() {
        try {
            List<Product> list = dbManager.loadAllProducts();

            tableModel.setRowCount(0);
            for (Product p : list) {
                tableModel.addRow(p.toRowData());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parentFrame, e.getMessage(), "Lỗi DB", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addProduct() {
        JTextField txtId = new JTextField();
        JTextField txtName = new JTextField();
        JTextField txtPrice = new JTextField();
        JTextField txtQty = new JTextField();
        JComboBox<String> cbType = new JComboBox<>(new String[]{"FOOD", "DRINK"});
        JTextField txtExtra = new JTextField();

        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
        panel.add(new JLabel("ID:")); panel.add(txtId);
        panel.add(new JLabel("Tên:")); panel.add(txtName);
        panel.add(new JLabel("Loại")); panel.add(txtPrice);
        panel.add(new JLabel("Giá:")); panel.add(txtQty);
        panel.add(new JLabel("Số Lượng:")); panel.add(cbType);
        panel.add(new JLabel("Giá bán:")); panel.add(txtExtra);

        int option = JOptionPane.showConfirmDialog(parentFrame, panel, "Thêm sản phẩm", JOptionPane.OK_CANCEL_OPTION);

        if (option != JOptionPane.OK_OPTION) return;

        try {
            String id = txtId.getText().trim();
            String name = txtName.getText().trim();
            double price = Double.parseDouble(txtPrice.getText().trim());
            int qty = Integer.parseInt(txtQty.getText().trim());
            String type = cbType.getSelectedItem().toString();
            String extra = txtExtra.getText().trim();

            Product p;

            if (type.equals("FOOD")) {
                p = new FoodProduct(id, name, price, qty, extra);
            } else {
                double volume = 0;
                try { volume = Double.parseDouble(extra); } catch (Exception ignored) {}
                p = new DrinkProduct(id, name, price, qty, volume);
            }

            dbManager.addProduct(p);
            loadTable();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parentFrame, ex.getMessage(), "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteProduct() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(parentFrame, "Hãy chọn 1 sản phẩm để xóa!");
            return;
        }

        String id = tableModel.getValueAt(row, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(parentFrame,
                "Xóa sản phẩm ID: " + id + "?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            dbManager.deleteProduct(id);
            loadTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parentFrame, e.getMessage(), "Lỗi DB", JOptionPane.ERROR_MESSAGE);
        }
    }
}
