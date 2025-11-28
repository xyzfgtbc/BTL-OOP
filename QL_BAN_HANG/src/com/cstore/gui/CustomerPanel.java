package com.cstore.gui;

import com.cstore.data.DBManager;
import com.cstore.model.Customer;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** Panel hiển thị và quản lý khách hàng. */
public class CustomerPanel extends JPanel {
    private DBManager dbManager;
    private JFrame parentFrame;
    private DefaultTableModel customerTableModel;
    private JTable customerTable;

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
        columnNames.add("ID");
        columnNames.add("Tên Khách Hàng");
        columnNames.add("SDT");
        columnNames.add("Điểm Tích Lũy");

        customerTableModel = new DefaultTableModel(columnNames, 0);
        customerTable = new JTable(customerTableModel);
        customerTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        customerTable.setRowHeight(25);
        customerTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        customerTable.setAutoCreateRowSorter(true);

        add(new JScrollPane(customerTable), BorderLayout.CENTER);
    }

    private void setupFunctionButtons() {
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnAdd = new JButton("Thêm Khách Hàng");
        JButton btnUpdate = new JButton("Cập Nhật Khách Hàng");
        JButton btnDelete = new JButton("Xóa Khách Hàng");
        JButton btnRefresh = new JButton("Làm Mới");

        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnUpdate.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 14));

        btnAdd.addActionListener(e -> showCustomerDialog(null));
        btnUpdate.addActionListener(e -> handleUpdateCustomer());
        btnDelete.addActionListener(e -> handleDeleteCustomer());
        btnRefresh.addActionListener(e -> refreshCustomerTable());

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnRefresh);

        add(btnPanel, BorderLayout.SOUTH);
    }

    public void refreshCustomerTable() {
        try {
            List<Customer> customers = dbManager.loadAllCustomers();
            customerTableModel.setRowCount(0);
            for (Customer c : customers) {
                customerTableModel.addRow(c.toRowData());
            }
            customerTableModel.fireTableDataChanged();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parentFrame, "Lỗi khi tải dữ liệu khách hàng từ DB: " + e.getMessage(), "Lỗi DB", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Xử lý thêm/cập nhật khách hàng
    private void showCustomerDialog(Customer customerToEdit) {
        JTextField txtName = new JTextField();
        JTextField txtPhone = new JTextField();

        if (customerToEdit != null) {
            txtName.setText(customerToEdit.getName());
            txtPhone.setText(customerToEdit.getPhoneNumber());
        }

        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("Tên:"));
        panel.add(txtName);
        panel.add(new JLabel("SĐT:"));
        panel.add(txtPhone);

        int result = JOptionPane.showConfirmDialog(this, panel,
                customerToEdit == null ? "Thêm Khách Hàng" : "Cập Nhật Khách Hàng",
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String name = txtName.getText().trim();
            String phone = txtPhone.getText().trim();
            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tên và SĐT không được bỏ trống!");
                return;
            }

            try {
                if (customerToEdit == null) {
                    dbManager.addCustomer(new Customer(name, phone));
                } else {
                    customerToEdit.setName(name);
                    customerToEdit.setPhoneNumber(phone);
                    dbManager.updateCustomer(customerToEdit);
                }
                refreshCustomerTable();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu dữ liệu: " + e.getMessage());
            }
        }
    }

    private void handleUpdateCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng để cập nhật!");
            return;
        }
        Customer c = getCustomerFromRow(selectedRow);
        showCustomerDialog(c);
    }

    private void handleDeleteCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng để xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa khách hàng này?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Customer c = getCustomerFromRow(selectedRow);
            try {
                dbManager.deleteCustomer(c.getId());
                refreshCustomerTable();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + e.getMessage());
            }
        }
    }

    private Customer getCustomerFromRow(int row) {
        int id = (int) customerTable.getValueAt(row, 0);
        String name = (String) customerTable.getValueAt(row, 1);
        String phone = (String) customerTable.getValueAt(row, 2);
        int points = (int) customerTable.getValueAt(row, 3);
        return new Customer(id, name, phone, points);
    }

    // Hỗ trợ tìm khách hàng theo số điện thoại cho SalePanel
    public Customer findCustomerByPhone(String phone) {
        try {
            List<Customer> customers = dbManager.loadAllCustomers();
            for (Customer c : customers) {
                if (c.getPhoneNumber().equals(phone)) return c;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parentFrame, "Lỗi DB: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
}
