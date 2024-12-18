package DAO;

import java.sql.*;
import java.sql.SQLException;

import Model.Account;
import Util.ConnectionUtil;

public class AccountDAO {

    public boolean doesUsernameExist(String username) throws SQLException {
        String sql = "SELECT * FROM Account WHERE username = ?";
        try (Connection connection = ConnectionUtil.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    public Account createAccount(Account account) throws SQLException {
        String sql = "INSERT INTO Account (username, password) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getPassword());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 1) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    account.setAccount_id(generatedKeys.getInt(1));
                    return account;
                }
            }
        }
        return null;
    }

    public Account getAccountByCredentials(String username, String password) throws SQLException {
        String sql = "SELECT * FROM Account WHERE username = ? AND password = ?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Account account = new Account();
                account.setAccount_id(rs.getInt("account_id"));
                account.setUsername(rs.getString("username"));
                account.setPassword(rs.getString("password"));
                return account;
            }
        }
        return null;
    }
    
}
