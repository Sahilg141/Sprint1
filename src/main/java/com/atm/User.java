package com.atm;

public class User {
    private int userId;
    private String name;
    private String mobile;
    private String pin;
    private double balance;

    public User() {}

    public User(int userId, String name, String mobile, String pin, double balance) {
        this.userId = userId;
        this.name = name;
        this.mobile = mobile;
        this.pin = pin;
        this.balance = balance;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getPin() { return pin; }
    public void setPin(String pin) { this.pin = pin; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
}