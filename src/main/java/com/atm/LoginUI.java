package com.atm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class LoginUI extends JFrame {
    private ATMService atmService;
    private boolean kioskMode;
    private JButton loginBtn;
    private JTextField mobileField;
    private JPasswordField pinField;
    private JLabel statusLabel;

    public LoginUI(ATMService service) {
        this(service, false);
    }

    public LoginUI(ATMService service, boolean kioskMode) {
        this.atmService = service;
        this.kioskMode = kioskMode;
        initUI();
    }

    private void initUI() {
        setTitle("ATM Login");
        setSize(450, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        if (kioskMode) {
            setUndecorated(true);
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            addExitKioskShortcut();
        }

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(10, 33, 68));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Logo
        JLabel logoLabel = new JLabel("🏦 ATM Simulator", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logoLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(logoLabel, gbc);

        // Mobile
        JLabel mobileLabel = new JLabel("Mobile Number:");
        mobileLabel.setForeground(Color.WHITE);
        mobileLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        mainPanel.add(mobileLabel, gbc);

        mobileField = new JTextField(15);
        mobileField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mobileField.setToolTipText("Enter your registered mobile number");
        gbc.gridx = 1; gbc.gridy = 1;
        mainPanel.add(mobileField, gbc);

        // PIN
        JLabel pinLabel = new JLabel("PIN:");
        pinLabel.setForeground(Color.WHITE);
        pinLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(pinLabel, gbc);

        pinField = new JPasswordField(15);
        pinField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pinField.setToolTipText("Enter your 4-digit PIN");
        gbc.gridx = 1; gbc.gridy = 2;
        mainPanel.add(pinField, gbc);

        // Login Button
        loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(15, 110, 190));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginBtn.setFocusPainted(false);
        loginBtn.setToolTipText("Click to login");
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        mainPanel.add(loginBtn, gbc);

        // Status Label
        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setForeground(Color.YELLOW);
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        mainPanel.add(statusLabel, gbc);

        add(mainPanel);

        loginBtn.addActionListener(this::handleLogin);

        getContentPane().setBackground(new Color(2, 24, 48));
        setVisible(true);
    }

    private void handleLogin(ActionEvent e) {
        String mobile = mobileField.getText().trim();
        String pin = new String(pinField.getPassword());

        if (mobile.isEmpty() || pin.isEmpty()) {
            statusLabel.setText("Please enter mobile and PIN.");
            return;
        }

        loginBtn.setEnabled(false);
        loginBtn.setText("Logging in...");
        statusLabel.setText("Authenticating...");

        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() throws Exception {
                return atmService.login(mobile, pin);
            }

            @Override
            protected void done() {
                try {
                    User user = get();
                    if (user != null) {
                        statusLabel.setText("Login successful!");
                        SwingUtilities.invokeLater(() -> {
                            new ATMUI(user, atmService, kioskMode);
                            dispose();
                        });
                    } else {
                        statusLabel.setText("Invalid credentials.");
                        JOptionPane.showMessageDialog(LoginUI.this, "Invalid credentials.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    statusLabel.setText("Login failed.");
                    JOptionPane.showMessageDialog(LoginUI.this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    loginBtn.setEnabled(true);
                    loginBtn.setText("Login");
                }
            }
        };
        worker.execute();
    }

    private void addExitKioskShortcut() {
        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK | KeyEvent.ALT_DOWN_MASK);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, "exitKiosk");
        getRootPane().getActionMap().put("exitKiosk", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(LoginUI.this, "Exiting kiosk mode", "Info", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new LoginUI(atmService, false);
            }
        });
    }
}
