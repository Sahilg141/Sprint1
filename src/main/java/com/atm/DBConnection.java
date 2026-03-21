package com.atm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.InputStream;
import java.util.Properties;

public class DBConnection {
    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection == null) {
            try {
                Properties props = new Properties();
                InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("db.properties");
                if (input == null) {
                    throw new RuntimeException("db.properties not found");
                }
                props.load(input);
                String url = props.getProperty("db.url");
                String user = props.getProperty("db.user");
                String password = props.getProperty("db.password");
                connection = DriverManager.getConnection(url, user, password);
            } catch (Exception e) {
                throw new SQLException("Failed to connect to database", e);
            }
        }
        return connection;
    }
}