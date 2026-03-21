CREATE DATABASE IF NOT EXISTS atm_db;
USE atm_db;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    mobile VARCHAR(10) UNIQUE NOT NULL,
    pin VARCHAR(4) NOT NULL,
    balance DOUBLE DEFAULT 0.0
);

CREATE TABLE IF NOT EXISTS transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    type ENUM('withdraw', 'deposit', 'upi_withdraw') NOT NULL,
    amount DOUBLE NOT NULL,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);