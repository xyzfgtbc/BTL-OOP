package com.cstore.main;

import com.cstore.data.DBManager;
import com.cstore.gui.CustomerPanel;
import com.cstore.gui.EmployeePanel;
import com.cstore.gui.InventoryPanel;
import com.cstore.gui.ReportPanel;
import com.cstore.gui.SalePanel;
import java.awt.*;
import java.sql.SQLException;
import javax.swing.*;

public class QLBH extends JFrame {

    private DBManager dbManager;

    public QLBH() {
    
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Khong the thiet lap Look and Feel he thong.");
        }

        
        dbManager = new DBManager();
        
        try {
            dbManager.insertSampleData(false);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Loi ket noi hoac chen du lieu mau: " + e.getMessage(), "Loi DB", JOptionPane.ERROR_MESSAGE);
        }

        setTitle("HE THONG QUAN LY CUA HANG TIEN LOI");
        setSize(1400, 800); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        
        JTabbedPane mainTabbedPane = new JTabbedPane();
        mainTabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        InventoryPanel inventoryPanel = new InventoryPanel(dbManager, this);
        CustomerPanel customerPanel = new CustomerPanel(dbManager, this); 
        EmployeePanel employeePanel = new EmployeePanel(dbManager, this);
        SalePanel salePanel = new SalePanel(dbManager, this, inventoryPanel, customerPanel); 
        ReportPanel reportPanel = new ReportPanel(dbManager, this);

        mainTabbedPane.addTab("Quan Ly Ton Kho", inventoryPanel);
        mainTabbedPane.addTab("Quan Ly Khach Hang", customerPanel);
        mainTabbedPane.addTab("Quan Ly Nhan Vien", employeePanel);
        mainTabbedPane.addTab("Tao Giao Dich Ban Hang", salePanel);
        mainTabbedPane.addTab("Bao Cao & Lich Su", reportPanel);
        
        add(mainTabbedPane, BorderLayout.CENTER);

        setVisible(true);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new QLBH());
    }
}