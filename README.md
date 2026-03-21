# ATM Simulation System (Java + MySQL + Swing)

A complete ATM simulation with 4-layer architecture:
- `DBConnection` (JDBC connection)
- `User`, `Transaction` model classes
- `ATMService`, `TransactionService` business logic
- Swing UI (`LoginUI`, `ATMUI`) and console fallback in `Main`

## 🚀 Requirements
- Java 17
- Maven 3.9+
- MySQL 8.0

## 🛠️ Setup
1. Create database:
   ```sql
   CREATE DATABASE atm_db;
   USE atm_db;
   ```
2. Load schema:
   - Execute `schema.sql` (creates `users` + `transactions`)
3. Add sample user:
   ```sql
   INSERT INTO users (name, mobile, pin, balance) VALUES ('John Doe', '1234567890', '1234', 1000.0);
   ```
4. Update DB config in `src/main/resources/db.properties`:
   ```properties
   db.url=jdbc:mysql://localhost:3306/atm_db
   db.user=root
   db.password=security
   ```

## 🧪 Build
```bash
mvn clean compile
```

## ▶️ Run
### Normal mode
```bash
mvn exec:java -Dexec.mainClass="com.atm.Main"
```

### Kiosk mode
```bash
mvn exec:java -Dexec.mainClass="com.atm.Main" -Dexec.args="kiosk"
```
- Exit kiosk: `Ctrl+Alt+C`

## 📦 Executable JAR
1. Build the executable JAR:
   ```bash
   mvn clean package
   ```

2. Run the JAR:
   ```bash
   java -jar target/atm-simulation-1.0-SNAPSHOT.jar
   ```

3. For kiosk mode:
   ```bash
   java -jar target/atm-simulation-1.0-SNAPSHOT.jar kiosk
   ```

## 🖥️ Windows EXE (Self-Contained)
1. Build the JAR as above.
2. Create the EXE:
   ```bash
   jpackage --name "ATM Simulation" --input target/ --main-jar atm-simulation-1.0-SNAPSHOT.jar --main-class com.atm.Main --type app-image --dest dist/
   ```
   - This requires JDK 14+ (you have 17).
   - Creates `dist/ATM Simulation/ATM Simulation.exe` (a self-contained app with embedded JRE).

3. Run the EXE:
   - Double-click `ATM Simulation.exe` or run from command line.
   - For kiosk mode: `"ATM Simulation.exe" kiosk` (pass arguments after the exe name).

## 🧾 Features
- Login with mobile and PIN
- Withdraw/Deposit
- Balance inquiry
- Mini statement (last 5)
- PIN change with old PIN + confirmation
- UPI withdraw with generated QR and OTP check (sample OTP: 1234)
- Auto logout after transaction
- Modern blue/black UI theme

## 📁 GitHub friendly
- `.gitignore` created for Maven/IDE files
- this `README.md` explains setup + run

## 🛡️ Notes
- This project is designed for demo/test use, not production.
- Ensure MySQL is running and credentials are correct before running.


## Classes

- DBConnection: Database connection management
- User: User model
- Transaction: Transaction model
- TransactionService: Transaction operations
- ATMService: Business logic for ATM operations
- ATMUI: Swing UI for ATM menu
- LoginUI: Swing UI for login
- Main: Application entry point