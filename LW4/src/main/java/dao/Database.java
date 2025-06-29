package dao;

import javax.swing.*;
import java.sql.*;

public class Database {
    private static final String DB_URL = "jdbc:sqlite:ollivander.db";
    private static Database instance;

    private Database() {
        initializeDatabase();
    }

    public static synchronized Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            
            stmt.execute("PRAGMA foreign_keys = OFF");
            
            stmt.execute("CREATE TABLE IF NOT EXISTS cores (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "type TEXT UNIQUE NOT NULL," +
                    "price REAL NOT NULL," +
                    "quantity INTEGER NOT NULL)");
            
            stmt.execute("CREATE TABLE IF NOT EXISTS woods (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "type TEXT UNIQUE NOT NULL," +
                    "price REAL NOT NULL," +
                    "quantity INTEGER NOT NULL)");
            
            stmt.execute("CREATE TABLE IF NOT EXISTS wands (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "core_id INTEGER NOT NULL," +
                    "wood_id INTEGER NOT NULL," +
                    "price REAL NOT NULL," +
                    "creation_date TEXT NOT NULL," +
                    "sold BOOLEAN NOT NULL DEFAULT 0," +
                    "FOREIGN KEY(core_id) REFERENCES cores(id) ON DELETE RESTRICT," +
                    "FOREIGN KEY(wood_id) REFERENCES woods(id) ON DELETE RESTRICT)");
            
            stmt.execute("CREATE TABLE IF NOT EXISTS customers (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "wand_id INTEGER UNIQUE NOT NULL," +
                    "purchase_date TEXT NOT NULL," +
                    "FOREIGN KEY(wand_id) REFERENCES wands(id) ON DELETE RESTRICT)");
            
            stmt.execute("CREATE TABLE IF NOT EXISTS supplies (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "type TEXT NOT NULL," +
                    "date TEXT NOT NULL)");
            
            stmt.execute("PRAGMA foreign_keys = ON");
            
            initializeComponents();
            
        } catch (SQLException e) {
            handleSQLException("Ошибка инициализации базы данных", e);
            System.exit(1);
        }
    }

    private void initializeComponents() {
        try (Connection conn = getConnection()) {

            insertComponent(conn, "cores", 1, "Перо феникса", 100.0, 0);
            insertComponent(conn, "cores", 2, "Волос единорога", 80.0, 0);
            
            insertComponent(conn, "woods", 1, "Остролист", 50.0, 0);
            insertComponent(conn, "woods", 2, "Орешник", 40.0, 0);
            
        } catch (SQLException e) {
            handleSQLException("Ошибка инициализации компонентов", e);
        }
    }
    
    private void insertComponent(Connection conn, String table, int id, String type, double price, int quantity) 
            throws SQLException {
        String sql = "INSERT OR IGNORE INTO " + table + " (id, type, price, quantity) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, type);
            pstmt.setDouble(3, price);
            pstmt.setInt(4, quantity);
            pstmt.executeUpdate();
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
    
    public void clearAllData() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute("PRAGMA foreign_keys = OFF");
            
            stmt.execute("DELETE FROM customers");
            stmt.execute("DELETE FROM wands");
            stmt.execute("DELETE FROM supplies");
            
            stmt.execute("UPDATE cores SET quantity = 0");
            stmt.execute("UPDATE woods SET quantity = 0");
            
            stmt.execute("DELETE FROM sqlite_sequence WHERE name IN ('customers', 'wands', 'supplies')");
            
            stmt.execute("PRAGMA foreign_keys = ON");
            
            JOptionPane.showMessageDialog(null, "Все данные успешно очищены!");
        } catch (SQLException e) {
            handleSQLException("Ошибка очистки данных", e);
        }
    }
    
    private void handleSQLException(String message, SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(
            null, 
            message + ": " + e.getMessage(), 
            "Ошибка базы данных", 
            JOptionPane.ERROR_MESSAGE
        );
    }
}