package com.atm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class ATMService {
    private Connection conn;
    private TransactionService transactionService;
    private User currentUser;

    public ATMService() throws SQLException {
        this.conn = DBConnection.getConnection();
        this.transactionService = new TransactionService();
    }

    public User login(String mobile, String pin) throws SQLException {
        String query = "SELECT * FROM users WHERE mobile = ? AND pin = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, mobile);
        stmt.setString(2, pin);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            User user = new User();
            user.setUserId(rs.getInt("id"));
            user.setName(rs.getString("name"));
            user.setMobile(rs.getString("mobile"));
            user.setPin(rs.getString("pin"));
            user.setBalance(rs.getDouble("balance"));
            return user;
        }
        return null;
    }

    public void checkBalance(User user) throws SQLException {
        String query = "SELECT balance FROM users WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, user.getUserId());
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            double balance = rs.getDouble("balance");
            System.out.println("Current balance: " + balance);
        }
    }

    public double getBalanceAmount(User user) throws SQLException {
        String query = "SELECT balance FROM users WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, user.getUserId());
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getDouble("balance");
        }
        throw new SQLException("User balance not found");
    }

    public boolean withdraw(User user, double amount) throws SQLException {
        conn.setAutoCommit(false);
        try {
            String balanceQuery = "SELECT balance FROM users WHERE id = ?";
            PreparedStatement balanceStmt = conn.prepareStatement(balanceQuery);
            balanceStmt.setInt(1, user.getUserId());
            ResultSet rs = balanceStmt.executeQuery();
            if (rs.next()) {
                double balance = rs.getDouble("balance");
                if (balance >= amount) {
                    String updateBalance = "UPDATE users SET balance = balance - ? WHERE id = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateBalance);
                    updateStmt.setDouble(1, amount);
                    updateStmt.setInt(2, user.getUserId());
                    updateStmt.executeUpdate();

                    String insertTrans = "INSERT INTO transactions (user_id, type, amount) VALUES (?, 'withdraw', ?)";
                    PreparedStatement transStmt = conn.prepareStatement(insertTrans);
                    transStmt.setInt(1, user.getUserId());
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

    public boolean deposit(User user, double amount) throws SQLException {
        conn.setAutoCommit(false);
        try {
            String updateBalance = "UPDATE users SET balance = balance + ? WHERE id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateBalance);
            updateStmt.setDouble(1, amount);
            updateStmt.setInt(2, user.getUserId());
            updateStmt.executeUpdate();

            String insertTrans = "INSERT INTO transactions (user_id, type, amount) VALUES (?, 'deposit', ?)";
            PreparedStatement transStmt = conn.prepareStatement(insertTrans);
            transStmt.setInt(1, user.getUserId());
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

    public void changePin(User user, String oldPin, String newPin) throws SQLException {
        if (!user.getPin().equals(oldPin)) {
            throw new SQLException("Old PIN is incorrect");
        }
        String query = "UPDATE users SET pin = ? WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, newPin);
        stmt.setInt(2, user.getUserId());
        stmt.executeUpdate();
        user.setPin(newPin);
    }

    public boolean upiWithdraw(User user, double amount) throws SQLException {
        conn.setAutoCommit(false);
        try {
            String balanceQuery = "SELECT balance FROM users WHERE id = ?";
            PreparedStatement balanceStmt = conn.prepareStatement(balanceQuery);
            balanceStmt.setInt(1, user.getUserId());
            ResultSet rs = balanceStmt.executeQuery();
            if (rs.next()) {
                double balance = rs.getDouble("balance");
                if (balance >= amount) {
                    String updateBalance = "UPDATE users SET balance = balance - ? WHERE id = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateBalance);
                    updateStmt.setDouble(1, amount);
                    updateStmt.setInt(2, user.getUserId());
                    updateStmt.executeUpdate();

                    String insertTrans = "INSERT INTO transactions (user_id, type, amount) VALUES (?, 'upi_withdraw', ?)";
                    PreparedStatement transStmt = conn.prepareStatement(insertTrans);
                    transStmt.setInt(1, user.getUserId());
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

    public void showMenu(Scanner scanner) {
        while (true) {
            System.out.println("\nATM Menu:");
            System.out.println("1. Withdraw");
            System.out.println("2. Deposit");
            System.out.println("3. Balance Inquiry");
            System.out.println("4. Mini Statement");
            System.out.println("5. PIN Change");
            System.out.println("6. UPI Withdraw");
            System.out.println("7. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    withdraw(scanner);
                    break;
                case 2:
                    deposit(scanner);
                    break;
                case 3:
                    balanceInquiry();
                    break;
                case 4:
                    miniStatement();
                    break;
                case 5:
                    changePin(scanner);
                    break;
                case 6:
                    upiWithdraw(scanner);
                    break;
                case 7:
                    System.out.println("Thank you for using ATM. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void withdraw(Scanner scanner) {
        System.out.print("Enter amount to withdraw: ");
        double amount = scanner.nextDouble();
        try {
            if (withdraw(currentUser, amount)) {
                System.out.println("Withdrawal successful.");
            } else {
                System.out.println("Insufficient balance or error occurred.");
            }
        } catch (SQLException e) {
            System.out.println("Error during withdrawal: " + e.getMessage());
        }
    }

    private void deposit(Scanner scanner) {
        System.out.print("Enter amount to deposit: ");
        double amount = scanner.nextDouble();
        try {
            if (deposit(currentUser, amount)) {
                System.out.println("Deposit successful.");
            } else {
                System.out.println("Error occurred during deposit.");
            }
        } catch (SQLException e) {
            System.out.println("Error during deposit: " + e.getMessage());
        }
    }

    private void balanceInquiry() {
        try {
            checkBalance(currentUser);
        } catch (SQLException e) {
            System.out.println("Error retrieving balance: " + e.getMessage());
        }
    }

    private void miniStatement() {
        try {
            var transactions = transactionService.getMiniStatement(currentUser.getUserId());
            System.out.println("Mini Statement:");
            for (Transaction t : transactions) {
                System.out.println(t.getDate() + " - " + t.getType() + " - " + t.getAmount());
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving statement: " + e.getMessage());
        }
    }

    private void changePin(Scanner scanner) {
        System.out.print("Enter current PIN: ");
        String oldPin = scanner.next();
        System.out.print("Enter new PIN: ");
        String newPin = scanner.next();
        System.out.print("Confirm new PIN: ");
        String confirmPin = scanner.next();

        if (!newPin.equals(confirmPin)) {
            System.out.println("New PIN and confirmation do not match.");
            return;
        }

        try {
            changePin(currentUser, oldPin, newPin);
            System.out.println("PIN changed successfully.");
            System.out.println("Logging out...");
            currentUser = null;
        } catch (SQLException e) {
            System.out.println("Error changing PIN: " + e.getMessage());
        }
    }

    private void upiWithdraw(Scanner scanner) {
        System.out.print("Enter amount for UPI Withdraw: ");
        double amount = scanner.nextDouble();
        System.out.print("Confirm UPI Withdraw of " + amount + "? (y/n): ");
        String confirm = scanner.next();
        if (confirm.equalsIgnoreCase("y")) {
            try {
                if (upiWithdraw(currentUser, amount)) {
                    System.out.println("UPI Withdraw successful.");
                } else {
                    System.out.println("Insufficient balance.");
                }
            } catch (SQLException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
}