import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

class BankAccount1 {
    private String accountNumber;
    private double balance;
    private String passwordHash;
    private String accountHolderName;

    public BankAccount(String accountNumber, String accountHolderName, String password) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = 0.0;
        this.passwordHash = hashPassword(password);
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public double getBalance() {
        return balance;
    }

    public boolean authenticate(String inputPassword) {
        return passwordHash.equals(hashPassword(inputPassword));
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("Deposited $" + amount + " into " + accountHolderName + "'s account successfully.");
            System.out.println("New balance: $" + balance);
        } else {
            System.out.println("Invalid deposit amount.");
        }
    }

    public void withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            System.out.println("Withdrawn $" + amount + " from " + accountHolderName + "'s account successfully.");
            System.out.println("New balance: $" + balance);
        } else {
            System.out.println("Invalid withdrawal amount or insufficient balance.");
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}

public class BankingApp {
    private static Map<String, BankAccount> accounts = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            boolean exit = false;

            while (!exit) {
                System.out.println("\nBanking Management Application");
                System.out.println("1. Create New Account");
                System.out.println("2. Deposit");
                System.out.println("3. Withdraw");
                System.out.println("4. View Balance");
                System.out.println("5. View All Account Details");
                System.out.println("6. Exit");
                System.out.print("Select an option (1-6): ");

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        createAccount(scanner);
                        break;
                    case 2:
                        performTransaction(scanner, "deposit");
                        break;
                    case 3:
                        performTransaction(scanner, "withdraw");
                        break;
                    case 4:
                        viewBalance(scanner);
                        break;
                    case 5:
                        viewAllAccounts();
                        break;
                    case 6:
                        exit = true;
                        System.out.println("Exiting the application. Thank you!");
                        break;
                    default:
                        System.out.println("Invalid option. Please select again.");
                }
            }
        }
    }

    private static void createAccount(Scanner scanner) {
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();
        if (accounts.containsKey(accountNumber)) {
            System.out.println("Account number already exists. Please choose a different account number.");
            return;
        }
        System.out.print("Enter account holder's name: ");
        String accountHolderName = scanner.nextLine();
        if (accountHolderName.isEmpty()) {
            System.out.println("Account holder's name cannot be empty.");
            return;
        }
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Confirm password: ");
        String confirmPassword = scanner.nextLine();

        if (password.equals(confirmPassword)) {
            accounts.put(accountNumber, new BankAccount(accountNumber, accountHolderName, password));
            System.out.println("Account created successfully.");
        } else {
            System.out.println("Password and confirm password do not match. Account creation failed.");
        }
    }

    private static void performTransaction(Scanner scanner, String transactionType) {
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();
        BankAccount account = accounts.get(accountNumber);
        if (account == null) {
            System.out.println("Account not found.");
            return;
        }
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        if (!account.authenticate(password)) {
            System.out.println("Authentication failed. Invalid password.");
            return;
        }

        System.out.print("Enter amount: $");
        double amount = scanner.nextDouble();
        if (transactionType.equals("deposit")) {
            account.deposit(amount);
        } else if (transactionType.equals("withdraw")) {
            account.withdraw(amount);
        }
    }

    private static void viewBalance(Scanner scanner) {
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();
        BankAccount account = accounts.get(accountNumber);
        if (account == null) {
            System.out.println("Account not found.");
            return;
        }
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        if (account.authenticate(password)) {
            double balance = account.getBalance();
            System.out.println("Account balance: $" + balance);
        } else {
            System.out.println("Authentication failed. Invalid password.");
        }
    }

    private static void viewAllAccounts() {
        System.out.println("Account Details:");
        for (BankAccount account : accounts.values()) {
            System.out.println("Account Holder's Name: " + account.getAccountHolderName());
            System.out.println("Account Number: " + account.getAccountNumber());
            System.out.println("Account Balance: $" + account.getBalance());
            System.out.println();
        }
    }
}
