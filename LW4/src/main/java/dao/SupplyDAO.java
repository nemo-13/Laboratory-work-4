package dao;

import model.Supply;
import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SupplyDAO {

    public void createSupply(String type, LocalDate date) {
        String sql = "INSERT INTO supplies (type, date) VALUES (?, ?)";
        
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, type);
            pstmt.setString(2, date.toString());
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            handleSQLException("Ошибка добавления поставки", e);
        }
    }

    public List<Supply> getAllSupplies() {
        List<Supply> supplies = new ArrayList<>();
        String sql = "SELECT * FROM supplies ORDER BY date DESC";
        
        try (Connection conn = Database.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                supplies.add(mapResultSetToSupply(rs));
            }
        } catch (SQLException e) {
            handleSQLException("Ошибка получения списка поставок", e);
        }
        return supplies;
    }

    public List<Supply> getSuppliesByType(String type) {
        List<Supply> supplies = new ArrayList<>();
        String sql = "SELECT * FROM supplies WHERE type = ? ORDER BY date DESC";
        
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, type);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    supplies.add(mapResultSetToSupply(rs));
                }
            }
        } catch (SQLException e) {
            handleSQLException("Ошибка получения поставок по типу", e);
        }
        return supplies;
    }

    public void deleteAllSupplies() {
        String sql = "DELETE FROM supplies";
        executeDelete(sql, "Ошибка удаления поставок");
    }

    private Supply mapResultSetToSupply(ResultSet rs) throws SQLException {
        return new Supply(
            rs.getInt("id"),
            rs.getString("type"),
            LocalDate.parse(rs.getString("date"))
        );
    }
    
    private void executeDelete(String sql, String errorMessage) {
        try (Connection conn = Database.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            handleSQLException(errorMessage, e);
        }
    }

    private void handleSQLException(String message, SQLException e) {
        System.err.println(message + ": " + e.getMessage());
        e.printStackTrace();
        JOptionPane.showMessageDialog(
            null, 
            message + ": " + e.getMessage(), 
            "Ошибка базы данных", 
            JOptionPane.ERROR_MESSAGE
        );
    }
}