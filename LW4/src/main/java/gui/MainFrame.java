package gui;

import javax.swing.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("Ollivander's Wand Shop");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Изготовление палочек", new WandsPanel());
        tabbedPane.addTab("Покупатели", new CustomersPanel());
        tabbedPane.addTab("Поставки материалов", new SupplyPanel());
        tabbedPane.addTab("Состояние склада", new InventoryPanel());
        tabbedPane.addTab("Очистка данных", new ClearDataDialog(this));
        
        add(tabbedPane);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            dao.Database.getInstance();
            new MainFrame().setVisible(true);
        });
    }
}