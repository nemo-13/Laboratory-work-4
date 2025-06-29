package gui;

import dao.CustomerDAO;
import dao.WandDAO;
import dao.CoreDAO;
import dao.WoodDAO;
import model.Core;
import model.Wood;
import model.Customer;
import model.Wand;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.time.LocalDate;

public class CustomersPanel extends JPanel {
    private JTable customerTable;
    private JScrollPane scrollPane;
    private JButton refreshButton;
    private JButton purchaseButton;

    public CustomersPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Покупатели", JLabel.LEFT);
        topPanel.add(titleLabel, BorderLayout.WEST);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshButton = new JButton("Обновить");
        refreshButton.addActionListener(e -> refreshData());
        
        purchaseButton = new JButton("Продать палочку");
        purchaseButton.addActionListener(e -> createPurchase());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(purchaseButton);
        topPanel.add(buttonPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        
        customerTable = new JTable();
        scrollPane = new JScrollPane(customerTable);
        add(scrollPane, BorderLayout.CENTER);
        
        refreshData();
    }
    
    private void refreshData() {
        CustomerDAO customerDAO = new CustomerDAO();
        List<Customer> customers = customerDAO.getAllCustomers();
        
        String[] columns = {"ID", "Имя покупателя", "ID палочки", "Дата покупки", "Цена (галлеоны)"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        WandDAO wandDAO = new WandDAO();
        
        for (Customer customer : customers) {
            Wand wand = wandDAO.getWandById(customer.getWandId());
            model.addRow(new Object[]{
                customer.getId(),
                customer.getName(),
                customer.getWandId(),
                customer.getPurchaseDate(),
                wand != null ? String.format("%.2f", wand.getPrice()) : "N/A"
            });
        }
        
        customerTable.setModel(model);
        
        revalidate();
        repaint();
    }
    
    private void createPurchase() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (parentFrame != null) {
            new PurchaseDialog(parentFrame).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(
                this, 
                "Не удалось определить родительское окно", 
                "Ошибка", 
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    private class PurchaseDialog extends JDialog {
        private JComboBox<Wand> wandCombo;
        private JTextField nameField;
        private CoreDAO coreDAO = new CoreDAO();
        private WoodDAO woodDAO = new WoodDAO();
        
        public PurchaseDialog(JFrame parent) {
            super(parent, "Продажа палочки", true);
            setSize(600, 250);
            setLocationRelativeTo(parent);
            setLayout(new GridLayout(4, 2, 10, 10));
            setResizable(false);
            
            WandDAO wandDAO = new WandDAO();
            List<Wand> availableWands = wandDAO.getAvailableWands();
            
            add(new JLabel("Выберите палочку:"));
            wandCombo = new JComboBox<>();
            for (Wand wand : availableWands) {
                wandCombo.addItem(wand);
            }
            
            wandCombo.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                                                              boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Wand) {
                        Wand w = (Wand) value;
                        Core core = coreDAO.getCoreById(w.getCoreId());
                        Wood wood = woodDAO.getWoodById(w.getWoodId());
                        setText(String.format("ID: %d | Сердцевина: %s | Древесина: %s | Цена: %.2f",
                                w.getId(),
                                core != null ? core.getType() : "?",
                                wood != null ? wood.getType() : "?",
                                w.getPrice()));
                    }
                    return this;
                }
            });
            
            add(new JScrollPane(wandCombo));
            
            add(new JLabel("Имя покупателя:"));
            nameField = new JTextField();
            add(nameField);

            JButton cancelButton = new JButton("Отмена");
            cancelButton.addActionListener(e -> dispose());
            
            JButton confirmButton = new JButton("Подтвердить продажу");
            confirmButton.addActionListener(e -> confirmPurchase());
            
            JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
            buttonPanel.add(cancelButton);
            buttonPanel.add(confirmButton);
            
            add(new JLabel());
            add(buttonPanel);
        }
        
        private void confirmPurchase() {
            Wand selectedWand = (Wand) wandCombo.getSelectedItem();
            String customerName = nameField.getText().trim();
            
            if (selectedWand == null) {
                JOptionPane.showMessageDialog(this, "Выберите палочку!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (customerName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введите имя покупателя!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            CustomerDAO customerDAO = new CustomerDAO();
            WandDAO wandDAO = new WandDAO();
            
            try {
                wandDAO.markAsSold(selectedWand.getId());
                customerDAO.addCustomer(customerName, selectedWand.getId(), LocalDate.now());
                
                JOptionPane.showMessageDialog(
                    this, 
                    "Продажа оформлена успешно!\n" +
                    "Палочка ID: " + selectedWand.getId() + "\n" +
                    "Покупатель: " + customerName,
                    "Успех", 
                    JOptionPane.INFORMATION_MESSAGE
                );
                dispose();
                refreshData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                    this, 
                    "Ошибка при оформлении продажи: " + e.getMessage(), 
                    "Ошибка", 
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
}