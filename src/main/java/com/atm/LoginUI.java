package com.atm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class LoginUI extends JFrame {
    private ATMService atmService;
    private boolean kioskMode;

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
        setSize(400, 220);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        if (kioskMode) {
            setUndecorated(true);
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            addExitKioskShortcut();
        }

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBackground(new Color(10, 33, 68));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel mobileLabel = new JLabel("Mobile:");
        mobileLabel.setForeground(Color.WHITE);
        JTextField mobileField = new JTextField();

        JLabel pinLabel = new JLabel("PIN:");
        pinLabel.setForeground(Color.WHITE);
        JPasswordField pinField = new JPasswordField();

        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(15, 110, 190));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));

        panel.add(mobileLabel);
        panel.add(mobileField);
        panel.add(pinLabel);
        panel.add(pinField);
        panel.add(new JLabel());
        panel.add(loginBtn);

        add(panel);

        loginBtn.addActionListener(e -> {
            String mobile = mobileField.getText();
            String pin = new String(pinField.getPassword());
            try {
                User user = atmService.login(mobile, pin);
                if (user != null) {
                    new ATMUI(user, atmService, kioskMode);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        getContentPane().setBackground(new Color(2, 24, 48));
        setVisible(true);
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
