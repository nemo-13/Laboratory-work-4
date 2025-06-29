package dao;

import model.Wood;
import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WoodDAO {

    public List<Wood> getAllWoods() {
        List<Wood> woods = new ArrayList<>();
        String sql = "SELECT * FROM woods ORDER BY type";
        
        try (Connection conn = Database.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                woods.add(mapResultSetToWood(rs));
            }
        } catch (SQLException e) {
            handleSQLException("Ошибка получения списка древесины", e);
        }
        return woods;
    }

    public Wood getWoodById(int id) {
        String sql = "SELECT * FROM woods WHERE id = ?";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToWood(rs);
                }
            }
        } catch (SQLException e) {
            handleSQLException("Ошибка получения древесины по ID", e);
        }
        return null;
    }

    public Wood getWoodByType(String type) {
        String sql = "SELECT * FROM woods WHERE type = ?";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, type);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToWood(rs);
                }
            }
        } catch (SQLException e) {
            handleSQLException("Ошибка получения древесины по типу", e);
        }
        return null;
    }

    public void updateWoodQuantity(int woodId, int newQuantity) {
        String sql = "UPDATE woods SET quantity = ? WHERE id = ?";
        
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, newQuantity);
            pstmt.setInt(2, woodId);
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Древесина не найдена, ID: " + woodId);
            }
        } catch (SQLException e) {
            handleSQLException("Ошибка обновления количества древесины", e);
        }
    }

    public void incrementWoodQuantity(int woodId, int amount) {
        String sql = "UPDATE woods SET quantity = quantity + ? WHERE id = ?";
        
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, amount);
            pstmt.setInt(2, woodId);
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Древесина не найдена, ID: " + woodId);
            }
        } catch (SQLException e) {
            handleSQLException("Ошибка увеличения количества древесины", e);
        }
    }

    public void updateWoodPrice(int woodId, double newPrice) {
        String sql = "UPDATE woods SET price = ? WHERE id = ?";
        
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, newPrice);
            pstmt.setInt(2, woodId);
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Древесина не найдена, ID: " + woodId);
            }
        } catch (SQLException e) {
            handleSQLException("Ошибка обновления цены древесины", e);
        }
    }

    public int getAvailableQuantity(int woodId) {
        String sql = "SELECT quantity FROM woods WHERE id = ?";
        
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, woodId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("quantity");
                }
            }
        } catch (SQLException e) {
            handleSQLException("Ошибка получения количества древесины", e);
        }
        return 0;
    }

    public boolean woodExists(int woodId) {
        String sql = "SELECT 1 FROM woods WHERE id = ?";
        
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, woodId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            handleSQLException("Ошибка проверки существования древесины", e);
        }
        return false;
    }

    public void resetAllWoodQuantities() {
        String sql = "UPDATE woods SET quantity = 0";
        
        try (Connection conn = Database.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            handleSQLException("Ошибка сброса количества древесины", e);
        }
    }

    private Wood mapResultSetToWood(ResultSet rs) throws SQLException {
        return new Wood(
            rs.getInt("id"),
            rs.getString("type"),
            rs.getDouble("price"),
            rs.getInt("quantity")
        );
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