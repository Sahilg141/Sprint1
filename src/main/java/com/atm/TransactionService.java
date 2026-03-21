package com.atm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransactionService {
    private Connection conn;

    public TransactionService() throws SQLException {
        this.conn = DBConnection.getConnection();
    }

    public boolean withdraw(int userId, double amount) throws SQLException {
        conn.setAutoCommit(false);
        try {
            // Check balance
            String balanceQuery = "SELECT balance FROM users WHERE id = ?";
            PreparedStatement balanceStmt = conn.prepareStatement(balanceQuery);
            balanceStmt.setInt(1, userId);
            ResultSet rs = balanceStmt.executeQuery();
            if (rs.next()) {
                double balance = rs.getDouble("balance");
                if (balance >= amount) {
                    // Update balance
                    String updateBalance = "UPDATE users SET balance = balance - ? WHERE id = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateBalance);
                    updateStmt.setDouble(1, amount);
                    updateStmt.setInt(2, userId);
                    updateStmt.executeUpdate();

                    // Insert transaction
                    String insertTrans = "INSERT INTO transactions (user_id, type, amount) VALUES (?, 'withdraw', ?)";
                    PreparedStatement transStmt = conn.prepareStatement(insertTrans);
                    transStmt.setInt(1, userId);
                    transStmt.setDouble(2, amount);
                    transStmt.executeUpdate();

                    conn.commit();
                    return true;
                }
            }
            conn.rollback();
            return false;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public boolean deposit(int userId, double amount) throws SQLException {
        conn.setAutoCommit(false);
        try {
            // Update balance
            String updateBalance = "UPDATE users SET balance = balance + ? WHERE id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateBalance);
            updateStmt.setDouble(1, amount);
            updateStmt.setInt(2, userId);
            updateStmt.executeUpdate();

            // Insert transaction
            String insertTrans = "INSERT INTO transactions (user_id, type, amount) VALUES (?, 'deposit', ?)";
            PreparedStatement transStmt = conn.prepareStatement(insertTrans);
            transStmt.setInt(1, userId);
            transStmt.setDouble(2, amount);
            transStmt.executeUpdate();

            conn.commit();
            return true;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public double getBalance(int userId) throws SQLException {
        String query = "SELECT balance FROM users WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, userId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getDouble("balance");
        }
        return 0.0;
    }

    public List<Transaction> getMiniStatement(int userId) throws SQLException {
        String query = "SELECT * FROM transactions WHERE user_id = ? ORDER BY date DESC LIMIT 5";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, userId);
        ResultSet rs = stmt.executeQuery();
        List<Transaction> transactions = new ArrayList<>();
        while (rs.next()) {
            Transaction t = new Transaction();
            t.setId(rs.getInt("id"));
            t.setUserId(rs.getInt("user_id"));
            t.setType(rs.getString("type"));
            t.setAmount(rs.getDouble("amount"));
            t.setDate(rs.getString("date"));
            transactions.add(t);
        }
        return transactions;
    }
}