package gui;

import dao.Database;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ClearDataDialog extends JPanel {
    private JButton clearButton;

    public ClearDataDialog(JFrame parent) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel warningLabel = new JLabel(
            "<html><center><b>Внимание!</b> Эта операция полностью очистит все данные:<br>" +
            "- История покупок<br>- Изготовленные палочки<br>- История поставок<br>" +
            "- Сбросит количество материалов на 0</center></html>",
            JLabel.CENTER
        );
        warningLabel.setForeground(Color.RED);
        
        clearButton = new JButton("Полная очистка данных");
        clearButton.setForeground(Color.RED);
        clearButton.addActionListener(e -> confirmClear(parent));
        
        add(warningLabel, BorderLayout.CENTER);
        add(clearButton, BorderLayout.SOUTH);
    }

    private void confirmClear(JFrame parent) {
        int result = JOptionPane.showConfirmDialog(
            parent,
            "Вы уверены, что хотите полностью очистить все данные?",
            "Подтверждение очистки",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            Database db = Database.getInstance();
            db.clearAllData();
            
            parent.dispose();
            new MainFrame().setVisible(true);
        }
    }
}