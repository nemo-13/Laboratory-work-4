package dao;

import model.Core;
import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CoreDAO {

    public List<Core> getAllCores() {
        List<Core> cores = new ArrayList<>();
        String sql = "SELECT * FROM cores ORDER BY type";
        
        try (Connection conn = Database.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                cores.add(mapResultSetToCore(rs));
            }
        } catch (SQLException e) {
            handleSQLException("Ошибка получения списка сердцевин", e);
        }
        return cores;
    }

    public Core getCoreById(int id) {
        String sql = "SELECT * FROM cores WHERE id = ?";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCore(rs);
                }
            }
        } catch (SQLException e) {
            handleSQLException("Ошибка получения сердцевины по ID", e);
        }
        return null;
    }

    public Core getCoreByType(String type) {
        String sql = "SELECT * FROM cores WHERE type = ?";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, type);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCore(rs);
                }
            }
        } catch (SQLException e) {
            handleSQLException("Ошибка получения сердцевины по типу", e);
        }
        return null;
    }

    public void updateCoreQuantity(int coreId, int newQuantity) {
        String sql = "UPDATE cores SET quantity = ? WHERE id = ?";
        
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, newQuantity);
            pstmt.setInt(2, coreId);
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Сердцевина не найдена, ID: " + coreId);
            }
        } catch (SQLException e) {
            handleSQLException("Ошибка обновления количества сердцевины", e);
        }
    }

    public void incrementCoreQuantity(int coreId, int amount) {
        String sql = "UPDATE cores SET quantity = quantity + ? WHERE id = ?";
        
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, amount);
            pstmt.setInt(2, coreId);
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Сердцевина не найдена, ID: " + coreId);
            }
        } catch (SQLException e) {
            handleSQLException("Ошибка увеличения количества сердцевины", e);
        }
    }

    public void updateCorePrice(int coreId, double newPrice) {
        String sql = "UPDATE cores SET price = ? WHERE id = ?";
        
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, newPrice);
            pstmt.setInt(2, coreId);
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Сердцевина не найдена, ID: " + coreId);
            }
        } catch (SQLException e) {
            handleSQLException("Ошибка обновления цены сердцевины", e);
        }
    }

    public int getAvailableQuantity(int coreId) {
        String sql = "SELECT quantity FROM cores WHERE id = ?";
        
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, coreId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("quantity");
                }
            }
        } catch (SQLException e) {
            handleSQLException("Ошибка получения количества сердцевины", e);
        }
        return 0;
    }

    public boolean coreExists(int coreId) {
        String sql = "SELECT 1 FROM cores WHERE id = ?";
        
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, coreId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            handleSQLException("Ошибка проверки существования сердцевины", e);
        }
        return false;
    }

    public void resetAllCoreQuantities() {
        String sql = "UPDATE cores SET quantity = 0";
        
        try (Connection conn = Database.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            handleSQLException("Ошибка сброса количества сердцевин", e);
        }
    }

    private Core mapResultSetToCore(ResultSet rs) throws SQLException {
        return new Core(
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