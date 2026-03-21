package com.atm;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        boolean kioskMode = args.length > 0 && "kiosk".equalsIgnoreCase(args[0]);
        try {
            ATMService atmService = new ATMService();
            new LoginUI(atmService, kioskMode);
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}