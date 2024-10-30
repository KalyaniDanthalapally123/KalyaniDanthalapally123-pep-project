package Service;

import DAO.AccountDAO;
import Model.Account;

import java.sql.*;


public class AccountService {
    private AccountDAO accountDAO;

    public AccountService(AccountDAO accountDAO){
        this.accountDAO = accountDAO;
    }

    public Account register(Account account) throws SQLException{
        if(account.getUsername()==null || account.getUsername().isEmpty()){
            throw new IllegalArgumentException("");
        }
        if(account.getPassword()==null || account.getPassword().length()<4){
            throw new IllegalArgumentException("");
        }
        if(accountDAO.isUserName(account.getUsername())){
            throw new IllegalArgumentException("");
        }
        return accountDAO.register(account);
    }

    public Account login(String username, String password) throws SQLException {
        Account account = accountDAO.login(username, password);
        if(username==null || password==null){
            return null;
        }
        if (account == null) {
            return null;
        }
        return account;
    }
}
