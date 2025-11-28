package com.cstore.gui;

import com.cstore.data.DBManager;
import com.cstore.model.Employee;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

/** Panel hien thi va quan ly Nhan vien. */
public class EmployeePanel extends JPanel {
    private DBManager dbManager;
    private JFrame parentFrame;
    private DefaultTableModel employeeTableModel;

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
        columnNames.add("ID"); columnNames.add("Ten Nhan Vien"); columnNames.add("Vi Tri"); columnNames.add("Luong Co Ban (VND)");

        employeeTableModel = new DefaultTableModel(columnNames, 0);
        JTable employeeTable = new JTable(employeeTableModel);
        employeeTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        employeeTable.setRowHeight(25);
        employeeTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        employeeTable.setAutoCreateRowSorter(true); 

        add(new JScrollPane(employeeTable), BorderLayout.CENTER);
    }
    
    private void setupFunctionButtons() {
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnAdd = new JButton("Them Nhan Vien"); JButton btnRefresh = new JButton("Lam Moi");
        
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14)); btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 14));

        btnAdd.addActionListener(e -> showAddEmployeeDialog()); btnRefresh.addActionListener(e -> refreshEmployeeTable());

        btnPanel.add(btnAdd); btnPanel.add(btnRefresh);
        add(btnPanel, BorderLayout.SOUTH);
    }

    public void refreshEmployeeTable() {
        try {
            List<Employee> employees = dbManager.loadAllEmployees();
            employeeTableModel.setRowCount(0); 
            for (Employee e : employees) { employeeTableModel.addRow(e.toRowData()); }
            employeeTableModel.fireTableDataChanged();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parentFrame, "Loi khi tai du lieu nhan vien tu DB: " + e.getMessage(), "Loi DB", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showAddEmployeeDialog() {
        // Xu ly them nhan vien
    }
}
