package com.cstore.gui;

import com.cstore.data.DBManager;
import com.cstore.model.Employee;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** Panel hiển thị và quản lý nhân viên. */
public class EmployeePanel extends JPanel {
    private DBManager dbManager;
    private JFrame parentFrame;
    private DefaultTableModel employeeTableModel;
    private JTable employeeTable;

    public EmployeePanel(DBManager dbManager, JFrame parentFrame) {
        this.dbManager = dbManager;
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout(10, 10));

        setupEmployeeTable();
        setupFunctionButtons();
        refreshEmployeeTable();
    }

    private void setupEmployeeTable() {
        Vector<String> columnNames = new Vector<>();
        columnNames.add("ID");
        columnNames.add("Tên Nhân Viên");
        columnNames.add("Vị Trí");
        columnNames.add("Lương Cơ Bản (VND)");

        employeeTableModel = new DefaultTableModel(columnNames, 0);
        employeeTable = new JTable(employeeTableModel);
        employeeTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        employeeTable.setRowHeight(25);
        employeeTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        employeeTable.setAutoCreateRowSorter(true);

        add(new JScrollPane(employeeTable), BorderLayout.CENTER);
    }

    private void setupFunctionButtons() {
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnAdd = new JButton("Thêm Nhân Viên");
        JButton btnDelete = new JButton("Xóa Nhân Viên");

        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 14));

        btnAdd.addActionListener(e -> showAddEmployeeDialog());
        btnDelete.addActionListener(e -> deleteSelectedEmployee());

        btnPanel.add(btnAdd);
        btnPanel.add(btnDelete);

        add(btnPanel, BorderLayout.SOUTH);
    }

    public void refreshEmployeeTable() {
        try {
            List<Employee> employees = dbManager.loadAllEmployees();
            employeeTableModel.setRowCount(0);
            for (Employee e : employees) {
                employeeTableModel.addRow(e.toRowData());
            }
            employeeTableModel.fireTableDataChanged();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parentFrame, "Lỗi khi tải dữ liệu nhân viên từ DB: " + e.getMessage(), "Lỗi DB", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddEmployeeDialog() {
        JTextField txtId = new JTextField();
        JTextField txtName = new JTextField();
        JTextField txtPosition = new JTextField();
        JTextField txtSalary = new JTextField();

        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.add(new JLabel("ID:")); panel.add(txtId);
        panel.add(new JLabel("Tên:")); panel.add(txtName);
        panel.add(new JLabel("Vị trí:")); panel.add(txtPosition);
        panel.add(new JLabel("Lương:")); panel.add(txtSalary);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Thêm Nhân Viên", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String id = txtId.getText().trim();
            String name = txtName.getText().trim();
            String position = txtPosition.getText().trim();
            double salary;
            try {
                salary = Double.parseDouble(txtSalary.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Lương phải là số hợp lệ!");
                return;
            }

            if (id.isEmpty() || name.isEmpty() || position.isEmpty()) {
                JOptionPane.showMessageDialog(this, "ID, Tên và Vị trí không được bỏ trống!");
                return;
            }

            try {
                dbManager.addEmployee(new Employee(id, name, position, salary));
                refreshEmployeeTable();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm nhân viên: " + e.getMessage());
            }
        }
    }

    private void deleteSelectedEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên để xóa!");
            return;
        }

        // Lấy ID nhân viên từ bảng (vị trí 0 của row)
        String employeeId = employeeTableModel.getValueAt(selectedRow, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa nhân viên này không?",
                "Xác nhận Xóa", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                dbManager.deleteEmployee(employeeId);
                refreshEmployeeTable();
                JOptionPane.showMessageDialog(this, "Xóa nhân viên thành công!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa nhân viên: " + e.getMessage());
            }
        }
    }
}
