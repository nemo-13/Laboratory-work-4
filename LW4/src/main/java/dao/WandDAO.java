package dao;

import model.Wand;
import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WandDAO {

    public void createWand(int coreId, int woodId, double price, LocalDate creationDate) {
        String sql = "INSERT INTO wands (core_id, wood_id, price, creation_date, sold) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, coreId);
            pstmt.setInt(2, woodId);
            pstmt.setDouble(3, price);
            pstmt.setString(4, creationDate.toString());
            pstmt.setBoolean(5, false);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            handleSQLException("Ошибка создания палочки", e);
        }
    }

    public List<Wand> getAllWands() {
        List<Wand> wands = new ArrayList<>();
        String sql = "SELECT * FROM wands";
        
        try (Connection conn = Database.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                wands.add(mapResultSetToWand(rs));
            }
        } catch (SQLException e) {
            handleSQLException("Ошибка получения списка палочек", e);
        }
        return wands;
    }

    public List<Wand> getAvailableWands() {
        List<Wand> wands = new ArrayList<>();
        String sql = "SELECT * FROM wands WHERE sold = 0";
        
        try (Connection conn = Database.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                wands.add(mapResultSetToWand(rs));
            }
        } catch (SQLException e) {
            handleSQLException("Ошибка получения доступных палочек", e);
        }
        return wands;
    }

    public Wand getWandById(int id) {
        String sql = "SELECT * FROM wands WHERE id = ?";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToWand(rs);
                }
            }
        } catch (SQLException e) {
            handleSQLException("Ошибка получения палочки по ID", e);
        }
        return null;
    }

    public void markAsSold(int wandId) {
        String sql = "UPDATE wands SET sold = 1 WHERE id = ?";
        
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, wandId);
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Палочка не найдена, ID: " + wandId);
            }
        } catch (SQLException e) {
            handleSQLException("Ошибка отметки о продаже палочки", e);
        }
    }

    public void deleteAllWands() {
        String sql = "DELETE FROM wands";
        executeDelete(sql, "Ошибка удаления палочек");
    }

    private Wand mapResultSetToWand(ResultSet rs) throws SQLException {
        return new Wand(
            rs.getInt("id"),
            rs.getInt("core_id"),
            rs.getInt("wood_id"),
            rs.getDouble("price"),
            LocalDate.parse(rs.getString("creation_date")),
            rs.getBoolean("sold")
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