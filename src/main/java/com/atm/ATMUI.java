package com.atm;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.sql.SQLException;

public class ATMUI extends JFrame {
    private User currentUser;
    private ATMService atmService;
    private boolean kioskMode;

    public ATMUI(User user, ATMService service) {
        this(user, service, false);
    }

    public ATMUI(User user, ATMService service, boolean kioskMode) {
        this.currentUser = user;
        this.atmService = service;
        this.kioskMode = kioskMode;
        initUI();
    }

    private void initUI() {
        setTitle("ATM Simulation");
        setSize(500, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ignored) {
        }

        JLabel header = new JLabel("ATM Dashboard", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(header, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 2, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton withdrawBtn = new JButton("Withdraw");
        JButton depositBtn = new JButton("Deposit");
        JButton balanceBtn = new JButton("Balance");
        JButton pinChangeBtn = new JButton("PIN Change");
        JButton upiBtn = new JButton("UPI Withdraw");
        JButton exitBtn = new JButton("Exit");

        buttonPanel.add(withdrawBtn);
        buttonPanel.add(depositBtn);
        buttonPanel.add(balanceBtn);
        buttonPanel.add(pinChangeBtn);
        buttonPanel.add(upiBtn);
        buttonPanel.add(exitBtn);

        add(buttonPanel, BorderLayout.CENTER);

        withdrawBtn.addActionListener(e -> withdraw());
        depositBtn.addActionListener(e -> deposit());
        balanceBtn.addActionListener(e -> checkBalance());
        pinChangeBtn.addActionListener(e -> changePin());
        upiBtn.addActionListener(e -> upiWithdraw());
        exitBtn.addActionListener(e -> System.exit(0));

        JLabel footer = new JLabel("Logged in as: " + currentUser.getName(), SwingConstants.CENTER);
        footer.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        add(footer, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void withdraw() {
        String amountStr = JOptionPane.showInputDialog(this, "Enter amount to withdraw:");
        if (amountStr != null) {
            try {
                double amount = Double.parseDouble(amountStr);
                if (atmService.withdraw(currentUser, amount)) {
                    JOptionPane.showMessageDialog(this, "Withdrawal successful.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    logout();
                } else {
                    JOptionPane.showMessageDialog(this, "Insufficient balance.", "Failed", JOptionPane.WARNING_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid amount.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void deposit() {
        String amountStr = JOptionPane.showInputDialog(this, "Enter amount to deposit:");
        if (amountStr != null) {
            try {
                double amount = Double.parseDouble(amountStr);
                if (atmService.deposit(currentUser, amount)) {
                    JOptionPane.showMessageDialog(this, "Deposit successful.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    logout();
                } else {
                    JOptionPane.showMessageDialog(this, "Error.", "Failed", JOptionPane.WARNING_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid amount.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void checkBalance() {
        try {
            double balance = atmService.getBalanceAmount(currentUser);
            JOptionPane.showMessageDialog(this, "Current balance: " + String.format("%.2f", balance), "Balance Inquiry", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void changePin() {
        String oldPin = JOptionPane.showInputDialog(this, "Enter current PIN:");
        if (oldPin == null) return;
        String newPin = JOptionPane.showInputDialog(this, "Enter new PIN:");
        if (newPin == null) return;
        String confirmPin = JOptionPane.showInputDialog(this, "Confirm new PIN:");
        if (confirmPin == null) return;

        if (!newPin.equals(confirmPin)) {
            JOptionPane.showMessageDialog(this, "New PIN and confirmation do not match.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            atmService.changePin(currentUser, oldPin, newPin);
            JOptionPane.showMessageDialog(this, "PIN changed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            logout();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void upiWithdraw() {
        String amountStr = JOptionPane.showInputDialog(this, "Enter amount for UPI Withdraw:");
        if (amountStr != null) {
            try {
                double amount = Double.parseDouble(amountStr);
                String qrData = String.format("upi://pay?pa=%s@bank&pn=%s&am=%.2f&cu=INR", currentUser.getMobile(), currentUser.getName(), amount);

                // Simulate QR by showing a text block and an icon that looks like a QR placeholder.
                Image qrImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = (Graphics2D) qrImage.getGraphics();
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, 200, 200);
                g.setColor(Color.BLACK);
                g.setStroke(new BasicStroke(4));
                g.drawRect(10, 10, 50, 50);
                g.drawRect(140, 10, 50, 50);
                g.drawRect(10, 140, 50, 50);
                g.drawLine(80, 80, 120, 120);
                g.drawLine(120, 80, 80, 120);
                g.dispose();

                JOptionPane.showMessageDialog(this, "Scan this simulated UPI QR:\n" + qrData, "UPI QR", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(qrImage));

                int confirm = JOptionPane.showConfirmDialog(this, "Confirm UPI Withdraw of " + String.format("%.2f", amount) + "?", "UPI Withdraw", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (atmService.upiWithdraw(currentUser, amount)) {
                        JOptionPane.showMessageDialog(this, "UPI Withdraw successful.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        logout();
                    } else {
                        JOptionPane.showMessageDialog(this, "Insufficient balance.", "Failed", JOptionPane.WARNING_MESSAGE);
                    }
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid amount.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    private void logout() {
        dispose();
        SwingUtilities.invokeLater(() -> new LoginUI(atmService));
    }
}
