package DAO;

import Model.Account;
import java.sql.*;

public class AccountDAO{
    private Connection conn;

    public AccountDAO(Connection conn){
        this.conn = conn;
    }

    public Account register(Account account) throws SQLException {
        String sql = "INSERT INTO Account (username, password) VALUES (?, ?)";
        try(PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getPassword());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()){
                account.setAccount_id(rs.getInt(1));
            }
            if(account.getUsername()==null){
                return null;
            }
            return account;
        }
    }

    public boolean isUserName(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Account WHERE username = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, username);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return rs.getInt(1)>0;
                }
            }
        }
        return false;
    }

    public Account findById(int accountId) throws SQLException {
        String sql = "SELECT * FROM Account WHERE account_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) { 
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Account(rs.getInt("account_id"), rs.getString("username"), rs.getString("password"));
            }
            return null;
        }
    }

    public Account login(String username, String password) throws SQLException {
        String sql = "SELECT * FROM Account WHERE username = ? AND password = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Account account = new Account();
                    account.setAccount_id(rs.getInt("account_id"));
                    account.setUsername(rs.getString("username"));
                    account.setPassword(rs.getString("password"));
                    return account;
                }
            }
        }
        return null;
    }
}