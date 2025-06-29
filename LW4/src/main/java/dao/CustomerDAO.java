package dao;

import model.Customer;
import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public void addCustomer(String name, int wandId, LocalDate purchaseDate) {
        String sql = "INSERT INTO customers (name, wand_id, purchase_date) VALUES (?, ?, ?)";
        
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.setInt(2, wandId);
            pstmt.setString(3, purchaseDate.toString());
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            handleSQLException("Ошибка добавления покупателя", e);
        }
    }

    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers";
        
        try (Connection conn = Database.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
        } catch (SQLException e) {
            handleSQLException("Ошибка получения списка покупателей", e);
        }
        return customers;
    }

    public Customer getCustomerByWandId(int wandId) {
        String sql = "SELECT * FROM customers WHERE wand_id = ?";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, wandId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCustomer(rs);
                }
            }
        } catch (SQLException e) {
            handleSQLException("Ошибка получения покупателя по ID палочки", e);
        }
        return null;
    }

    public void deleteAllCustomers() {
        String sql = "DELETE FROM customers";
        executeDelete(sql, "Ошибка удаления покупателей");
    }

    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        return new Customer(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getInt("wand_id"),
            LocalDate.parse(rs.getString("purchase_date"))
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