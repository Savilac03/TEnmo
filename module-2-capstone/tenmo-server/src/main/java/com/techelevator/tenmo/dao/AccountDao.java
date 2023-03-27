package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDao {

    List<Account> getAccounts ();
    Account getAccountByAccountID(int accountId);
    BigDecimal getBalance (int userId);
    void updateAccount(Account accountToUpdate);

    Account getAccountByUserID(int id);
}
