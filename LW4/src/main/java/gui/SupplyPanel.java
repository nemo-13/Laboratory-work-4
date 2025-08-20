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
        
        if ("monthly".equals(supplyType)) {
            int currentMonth = LocalDate.now().getMonthValue();
            if (currentMonth < 6 || currentMonth > 8) {
                log.append("Сезонная поставка возможна только летом (июнь-август)!");
                logArea.setText(log.toString());
                return;
            }
        }
        
        CoreDAO coreDAO = new CoreDAO();
        WoodDAO woodDAO = new WoodDAO();
        SupplyDAO supplyDAO = new SupplyDAO();
        
        List<Core> cores = coreDAO.getAllCores();
        List<Wood> woods = woodDAO.getAllWoods();
        
        log.append("Начало обработки ")
           .append(supplyType.equals("weekly") ? "еженедельной" : "сезонной")
           .append(" поставки\n");
        
        if ("weekly".equals(supplyType)) {
            for (Core core : cores) {
                int currentQuantity = coreDAO.getAvailableQuantity(core.getId());
                if (currentQuantity < 15) {
                    int amount = 15 - currentQuantity;
                    coreDAO.updateCoreQuantity(core.getId(), 15);
                    log.append("Сердцевина '").append(core.getType())
                       .append("' пополнена на ").append(amount)
                       .append(" (теперь 15)\n");
                }
                else {
                    log.append("Сердцевин '").append(core.getType())
                       .append("' достаточно\n");
                }
            }
            for (Wood wood : woods) {
                int currentQuantity = woodDAO.getAvailableQuantity(wood.getId());
                if (currentQuantity < 15) {
                    int amount = 15 - currentQuantity;
                    woodDAO.updateWoodQuantity(wood.getId(), 15);
                    log.append("Древесина '").append(wood.getType())
                       .append("' пополнена на ").append(amount)
                       .append(" (теперь 15)\n");
                }
                else {
                    log.append("Древесины '").append(wood.getType())
                       .append("' достаточно\n");
                }
            }
        } else {
            for (Core core : cores) {
                int currentQuantity = coreDAO.getAvailableQuantity(core.getId());
                coreDAO.updateCoreQuantity(core.getId(), currentQuantity + 25);
                log.append("Сердцевина '").append(core.getType())
                   .append("' увеличена на 25 (теперь ")
                   .append(currentQuantity + 25).append(")\n");
            }
            for (Wood wood : woods) {
                int currentQuantity = woodDAO.getAvailableQuantity(wood.getId());
                woodDAO.updateWoodQuantity(wood.getId(), currentQuantity + 25);
                log.append("Древесина '").append(wood.getType())
                   .append("' увеличена на 25 (теперь ")
                   .append(currentQuantity + 25).append(")\n");
            }
        }
        
        supplyDAO.createSupply(supplyType, LocalDate.now());
        log.append("Поставка успешно обработана!");
        
        logArea.setText(log.toString());
    }
}