package com.cstore.gui;

import com.cstore.data.DBManager;
import com.cstore.model.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** Panel tạo giao dịch bán hàng. */
public class SalePanel extends JPanel {

    private DBManager dbManager;
    private JFrame parentFrame;
    private InventoryPanel inventoryPanel;
    private CustomerPanel customerPanel;

    private DefaultTableModel saleTableModel;
    private JTable saleTable;
    private JTextField txtProductId;
    private JTextField txtQuantity;
    private JTextField txtPhoneNumber;
    private JLabel lblTotal;

    private List<Product> currentItems;
    private Customer currentCustomer;

    public SalePanel(DBManager dbManager, JFrame parentFrame, InventoryPanel inventoryPanel, CustomerPanel customerPanel) {
        this.dbManager = dbManager;
        this.parentFrame = parentFrame;
        this.inventoryPanel = inventoryPanel;
        this.customerPanel = customerPanel;

        currentItems = new ArrayList<>();
        currentCustomer = null;

        setLayout(new BorderLayout(10, 10));
        setupUI();
    }

    private void setupUI() {
        // Bảng hiển thị sản phẩm trong giao dịch
        saleTableModel = new DefaultTableModel(new Object[]{"ID", "Tên SP", "SL", "Đơn giá", "Thành tiền"}, 0);
        saleTable = new JTable(saleTableModel);
        add(new JScrollPane(saleTable), BorderLayout.CENTER);

        // Panel nhập liệu
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        txtProductId = new JTextField(6);
        txtQuantity = new JTextField(3);
        txtPhoneNumber = new JTextField(10);
        JButton btnAdd = new JButton("Thêm SP");
        JButton btnFinalize = new JButton("Thanh toán");
        JButton btnCancel = new JButton("Hủy giao dịch");
        JButton btnInvoice = new JButton("Xuất hóa đơn");
        lblTotal = new JLabel("Tổng: 0 VND");

        inputPanel.add(new JLabel("Mã SP:")); inputPanel.add(txtProductId);
        inputPanel.add(new JLabel("SL:")); inputPanel.add(txtQuantity);
        inputPanel.add(new JLabel("SĐT KH:")); inputPanel.add(txtPhoneNumber);
        inputPanel.add(btnAdd); inputPanel.add(btnFinalize); 
        inputPanel.add(btnCancel); inputPanel.add(btnInvoice);
        inputPanel.add(lblTotal);

        add(inputPanel, BorderLayout.NORTH);

        // Action thêm sản phẩm
        btnAdd.addActionListener(e -> addProductToSale());
        // Action thanh toán
        btnFinalize.addActionListener(e -> finalizeSale());
        // Action hủy giao dịch
        btnCancel.addActionListener(e -> cancelSale());
        // Action xuất hóa đơn
        btnInvoice.addActionListener(e -> printInvoice());
    }

    private void addProductToSale() {
        String productId = txtProductId.getText().trim();
        int quantity;
        try {
            quantity = Integer.parseInt(txtQuantity.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng phải là số nguyên!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (quantity <= 0) return;

        try {
            Product product = null;
            for (Product p : dbManager.loadAllProducts()) {
                if (p.getId().equalsIgnoreCase(productId)) {
                    product = p;
                    break;
                }
            }
            if (product == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Thêm vào danh sách hiện tại
            Product copy = new FoodProduct(product.getId(), product.getName(), product.getBasePrice(), quantity, "");
            currentItems.add(copy);

            saleTableModel.addRow(new Object[]{
                product.getId(),
                product.getName(),
                quantity,
                String.format("%,.0f", product.getBasePrice()),
                String.format("%,.0f", product.getBasePrice() * quantity)
            });

            updateTotal();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    private void updateTotal() {
        double total = 0;
        for (Product p : currentItems) {
            total += p.getBasePrice() * p.getQuantity();
        }
        lblTotal.setText("Tổng: " + String.format("%,.0f", total) + " VND");
    }

    private void finalizeSale() {
    if (currentItems.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Chưa có sản phẩm nào!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    String phone = txtPhoneNumber.getText().trim();
    currentCustomer = null;
    if (!phone.isEmpty()) {
        try {
            currentCustomer = dbManager.findCustomerByPhone(phone);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tìm khách hàng: " + e.getMessage());
            return;
        }
    }

    double total = 0;
    for (Product p : currentItems) total += p.getBasePrice() * p.getQuantity();

    String transId = "T" + System.currentTimeMillis();
    Integer custId = currentCustomer != null ? currentCustomer.getId() : null;

    String employeeId = "123"; // Nếu không quan tâm đến nhân viên, vẫn cần giá trị hợp lệ
    SalesTransaction transaction = new SalesTransaction(transId, employeeId, custId, LocalDateTime.now(), total);

    try {
        // Lưu giao dịch
        dbManager.saveTransaction(transaction, currentItems, custId);

        // **CẬP NHẬT SỐ LƯỢNG SAU KHI THANH TOÁN**
        for (Product p : currentItems) {
            dbManager.decreaseProductQuantity(p.getId(), p.getQuantity());
        }

        // Cập nhật lại bảng InventoryPanel nếu có
        if (inventoryPanel != null) {
            inventoryPanel.loadTable();
        }

        JOptionPane.showMessageDialog(this, "Thanh toán thành công!\nTổng: " + String.format("%,.0f", total) + " VND");

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Lỗi khi lưu giao dịch: " + e.getMessage());
        return;
    }

    currentItems.clear();
    saleTableModel.setRowCount(0);
    txtProductId.setText(""); txtQuantity.setText(""); txtPhoneNumber.setText("");
    lblTotal.setText("Tổng: 0 VND");
}


    private void cancelSale() {
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn hủy giao dịch?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            currentItems.clear();
            saleTableModel.setRowCount(0);
            txtProductId.setText(""); txtQuantity.setText(""); txtPhoneNumber.setText("");
            lblTotal.setText("Tổng: 0 VND");
        }
    }

    private void printInvoice() {
        if (currentItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chưa có sản phẩm để xuất hóa đơn!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("==== HÓA ĐƠN BÁN HÀNG ====\n");
        sb.append("Ngày: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy"))).append("\n");
        sb.append("Khách hàng: ").append(currentCustomer != null ? currentCustomer.getName() : "Vãng lai").append("\n");
        sb.append("-----------------------------\n");

        double total = 0;
        for (Product p : currentItems) {
            double lineTotal = p.getBasePrice() * p.getQuantity();
            total += lineTotal;
            sb.append(String.format("%s x%d - %, .0f VND\n", p.getName(), p.getQuantity(), lineTotal));
        }

        sb.append("-----------------------------\n");
        sb.append(String.format("Tổng: %, .0f VND\n", total));
        sb.append("=============================\n");

        JOptionPane.showMessageDialog(this, sb.toString(), "HÓA ĐƠN", JOptionPane.INFORMATION_MESSAGE);
    }
}
