package gui;

import dao.CoreDAO;
import dao.WandDAO;
import dao.WoodDAO;
import model.Core;
import model.Wood;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class WandsPanel extends JPanel {
    private JComboBox<String> coreCombo;
    private JComboBox<String> woodCombo;
    private JButton createButton;
    private JButton refreshButton;
    private JLabel statusLabel;
    private List<Core> cores;
    private List<Wood> woods;

    public WandsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshButton = new JButton("Обновить");
        refreshButton.addActionListener(e -> refreshData());
        topPanel.add(refreshButton);
        add(topPanel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel selectionPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        selectionPanel.setBorder(BorderFactory.createTitledBorder("Выбор компонентов"));
        
        coreCombo = new JComboBox<>();
        woodCombo = new JComboBox<>();
        
        selectionPanel.add(new JLabel("Сердцевина:"));
        selectionPanel.add(coreCombo);
        selectionPanel.add(new JLabel("Древесина:"));
        selectionPanel.add(woodCombo);
        
        mainPanel.add(selectionPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        createButton = new JButton("Создать палочку");
        createButton.addActionListener(e -> createWand());
        buttonPanel.add(createButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);

        statusLabel = new JLabel(" ", JLabel.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        refreshData();
    }

    private void refreshData() {
        CoreDAO coreDAO = new CoreDAO();
        WoodDAO woodDAO = new WoodDAO();
        
        cores = coreDAO.getAllCores();
        woods = woodDAO.getAllWoods();
        
        coreCombo.removeAllItems();
        woodCombo.removeAllItems();
        
        for (Core core : cores) {
            coreCombo.addItem(core.getType() + " (" + core.getQuantity() + " шт)");
        }
        
        for (Wood wood : woods) {
            woodCombo.addItem(wood.getType() + " (" + wood.getQuantity() + " шт)");
        }
        
        statusLabel.setText("Данные обновлены");
    }

    private void createWand() {
        int coreIndex = coreCombo.getSelectedIndex();
        int woodIndex = woodCombo.getSelectedIndex();
        
        if (coreIndex == -1 || woodIndex == -1) {
            statusLabel.setText("Выберите компоненты!");
            return;
        }
        
        Core selectedCore = cores.get(coreIndex);
        Wood selectedWood = woods.get(woodIndex);

        if (selectedCore.getQuantity() < 1) {
            statusLabel.setText("Недостаточно сердцевин: " + selectedCore.getType());
            return;
        }
        
        if (selectedWood.getQuantity() < 1) {
            statusLabel.setText("Недостаточно древесины: " + selectedWood.getType());
            return;
        }

        double price = (selectedCore.getPrice() + selectedWood.getPrice()) * 1.1;
        
        WandDAO wandDAO = new WandDAO();
        wandDAO.createWand(selectedCore.getId(), selectedWood.getId(), price, LocalDate.now());

        CoreDAO coreDAO = new CoreDAO();
        coreDAO.updateCoreQuantity(selectedCore.getId(), selectedCore.getQuantity() - 1);
        
        WoodDAO woodDAO = new WoodDAO();
        woodDAO.updateWoodQuantity(selectedWood.getId(), selectedWood.getQuantity() - 1);

        refreshData();
        statusLabel.setText("Палочка создана! Цена: " + String.format("%.2f", price) + " галлеонов");
    }
}