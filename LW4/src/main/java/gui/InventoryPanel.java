package gui;

import dao.CoreDAO;
import dao.WoodDAO;
import dao.WandDAO;
import model.Core;
import model.Wood;
import model.Wand;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InventoryPanel extends JPanel {
    private JButton refreshButton;
    private JPanel tablesPanel;

    public InventoryPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshButton = new JButton("Обновить");
        refreshButton.addActionListener(e -> refreshData());
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.NORTH);

        tablesPanel = new JPanel();
        tablesPanel.setLayout(new BoxLayout(tablesPanel, BoxLayout.Y_AXIS));
        add(new JScrollPane(tablesPanel), BorderLayout.CENTER);

        refreshData();
    }
    
    private void refreshData() {
        tablesPanel.removeAll();

        CoreDAO coreDAO = new CoreDAO();
        WoodDAO woodDAO = new WoodDAO();
        WandDAO wandDAO = new WandDAO();
        
        List<Core> cores = coreDAO.getAllCores();
        List<Wood> woods = woodDAO.getAllWoods();
        List<Wand> wands = wandDAO.getAllWands();

        addTable("Сердцевины", new String[]{"Тип сердцевины", "Цена (галлеоны)", "Количество"}, cores, 
            core -> new Object[]{core.getType(), String.format("%.2f", core.getPrice()), core.getQuantity()});

        addTable("Древесина", new String[]{"Тип древесины", "Цена (галлеоны)", "Количество"}, woods, 
            wood -> new Object[]{wood.getType(), String.format("%.2f", wood.getPrice()), wood.getQuantity()});

        addWandTable("Готовые палочки", wands);
        
        tablesPanel.revalidate();
        tablesPanel.repaint();
    }
    
    private <T> void addTable(String title, String[] columns, List<T> items, java.util.function.Function<T, Object[]> mapper) {
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        for (T item : items) {
            model.addRow(mapper.apply(item));
        }
        
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder(title));
        scrollPane.setPreferredSize(new Dimension(0, 150));
        
        tablesPanel.add(scrollPane);
    }
    
    private void addWandTable(String title, List<Wand> wands) {
        String[] columns = {"ID", "Сердцевина", "Древесина", "Цена (галлеоны)", "Дата создания", "Статус"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        CoreDAO coreDAO = new CoreDAO();
        WoodDAO woodDAO = new WoodDAO();
        
        for (Wand wand : wands) {
            Core core = coreDAO.getCoreById(wand.getCoreId());
            Wood wood = woodDAO.getWoodById(wand.getWoodId());
            
            model.addRow(new Object[]{
                wand.getId(),
                core != null ? core.getType() : "N/A",
                wood != null ? wood.getType() : "N/A",
                String.format("%.2f", wand.getPrice()),
                wand.getCreationDate(),
                wand.isSold() ? "Продана" : "В наличии"
            });
        }
        
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder(title));
        scrollPane.setPreferredSize(new Dimension(0, 200));
        
        tablesPanel.add(scrollPane);
    }
}