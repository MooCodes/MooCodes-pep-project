package Service;

import java.sql.SQLException;

import DAO.AccountDAO;
import Model.Account;

public class AccountService {
    private AccountDAO accountDAO = new AccountDAO();
    
    // Validate and create an account
    public Account registerAccount(String username, String password) throws SQLException {
        System.out.println("From register account");
        // Validate the input fields
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException();
        }
        if (password == null || password.length() < 4) {
            System.out.println("Less than 4 chars");
            throw new IllegalArgumentException();
        }

        // Check if the username already exists
        if (accountDAO.doesUsernameExist(username)) {
            throw new IllegalArgumentException();
        }

        // Create the account if all conditions are met
        Account newAccount = new Account();
        newAccount.setUsername(username);
        newAccount.setPassword(password);

        return accountDAO.createAccount(newAccount);
    }

        // Verify login credentials
        public Account login(String username, String password) throws SQLException {
            // Validate inputs
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("Username cannot be blank.");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new IllegalArgumentException("Password cannot be blank.");
            }
    
            // Retrieve account from database
            Account account = accountDAO.getAccountByCredentials(username, password);
    
            // If no account is found, throw an exception
            if (account == null) {
                throw new SecurityException("Invalid username or password.");
            }
    
            return account;
        }
}
