package com.cstore.gui;

import com.cstore.data.DBManager;
import com.cstore.model.SalesTransaction;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

/** Panel Báo Cáo và Lịch Sử Giao Dịch. */
public class ReportPanel extends JPanel {
    private DBManager dbManager;
    private JFrame parentFrame;
    private DefaultTableModel reportTableModel;
    
    public ReportPanel(DBManager dbManager, JFrame parentFrame) {
        this.dbManager = dbManager;
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());
        setupReportUI();
    }
    
    private void setupReportUI() {
        reportTableModel = new DefaultTableModel(new Object[]{"Mã GD", "ID NV", "ID KH", "Thời Gian", "Tổng Tiền (VND)"}, 0);
        JTable reportTable = new JTable(reportTableModel);
        reportTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        reportTable.setRowHeight(25);
        reportTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        reportTable.setAutoCreateRowSorter(true);
        
        add(new JScrollPane(reportTable), BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnLoadReports = new JButton("Tải Lịch Sử Giao Dịch");
        btnLoadReports.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        btnLoadReports.addActionListener(e -> loadTransactionHistory());
        
        btnPanel.add(btnLoadReports);
        add(btnPanel, BorderLayout.SOUTH);
    }
    
    private void loadTransactionHistory() {
        try {
            List<SalesTransaction> transactions = dbManager.loadAllTransactions();
            reportTableModel.setRowCount(0);
            for (SalesTransaction t : transactions) {
                reportTableModel.addRow(t.toRowData());
            }
            JOptionPane.showMessageDialog(parentFrame, "Đã tải " + transactions.size() + " giao dịch.", "Tải Báo Cáo", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(parentFrame, "Lỗi khi tải lịch sử giao dịch: " + ex.getMessage(), "Lỗi DB", JOptionPane.ERROR_MESSAGE);
        }
    }
}
