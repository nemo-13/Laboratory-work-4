package gui;

import dao.CoreDAO;
import dao.SupplyDAO;
import dao.WoodDAO;
import model.Core;
import model.Wood;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.util.List;

public class SupplyPanel extends JPanel {
    private JButton weeklyButton;
    private JButton monthlyButton;
    private JButton refreshButton;
    private JTextArea logArea;

    public SupplyPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshButton = new JButton("Обновить");
        refreshButton.addActionListener(e -> logArea.setText(""));
        topButtonPanel.add(refreshButton);
        add(topButtonPanel, BorderLayout.NORTH);

        JPanel mainButtonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        weeklyButton = new JButton("Еженедельная поставка");
        monthlyButton = new JButton("Ежемесячная поставка");
        
        weeklyButton.addActionListener(this::processSupply);
        monthlyButton.addActionListener(this::processSupply);
        
        mainButtonPanel.add(weeklyButton);
        mainButtonPanel.add(monthlyButton);
        
        add(mainButtonPanel, BorderLayout.SOUTH);

        logArea = new JTextArea(10, 40);
        logArea.setEditable(false);
        add(new JScrollPane(logArea), BorderLayout.CENTER);
    }

    private void processSupply(ActionEvent e) {
        String supplyType = e.getSource() == weeklyButton ? "weekly" : "monthly";
        StringBuilder log = new StringBuilder();
        
        CoreDAO coreDAO = new CoreDAO();
        WoodDAO woodDAO = new WoodDAO();
        SupplyDAO supplyDAO = new SupplyDAO();
        
        List<Core> cores = coreDAO.getAllCores();
        List<Wood> woods = woodDAO.getAllWoods();
        
        log.append("Начало обработки ")
           .append(supplyType.equals("weekly") ? "еженедельной" : "ежемесячной")
           .append(" поставки\n");
        
        if ("weekly".equals(supplyType)) {
            for (Core core : cores) {
                if (core.getQuantity() < 20) {
                    int amount = 20 - core.getQuantity();
                    coreDAO.updateCoreQuantity(core.getId(), 20);
                    log.append("Сердцевина '").append(core.getType())
                       .append("' пополнена на ").append(amount)
                       .append(" (теперь 20)\n");
                }
            }
            for (Wood wood : woods) {
                if (wood.getQuantity() < 20) {
                    int amount = 20 - wood.getQuantity();
                    woodDAO.updateWoodQuantity(wood.getId(), 20);
                    log.append("Древесина '").append(wood.getType())
                       .append("' пополнена на ").append(amount)
                       .append(" (теперь 20)\n");
                }
            }
        } else {
            for (Core core : cores) {
                coreDAO.updateCoreQuantity(core.getId(), core.getQuantity() + 25);
                log.append("Сердцевина '").append(core.getType())
                   .append("' увеличена на 25 (теперь ")
                   .append(core.getQuantity() + 25).append(")\n");
            }
            for (Wood wood : woods) {
                woodDAO.updateWoodQuantity(wood.getId(), wood.getQuantity() + 25);
                log.append("Древесина '").append(wood.getType())
                   .append("' увеличена на 25 (теперь ")
                   .append(wood.getQuantity() + 25).append(")\n");
            }
        }
        
        supplyDAO.createSupply(supplyType, LocalDate.now());
        log.append("Поставка успешно обработана!");
        
        logArea.setText(log.toString());
    }
}