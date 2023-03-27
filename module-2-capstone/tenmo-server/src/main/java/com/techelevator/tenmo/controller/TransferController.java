package com.techelevator.tenmo.controller;


import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/account/transfers")
@PreAuthorize("isAuthenticated()")
public class TransferController {

    private AccountDao accountDao;
    private TransferDao transferDao;
    private UserDao userDao;


    public TransferController(TransferDao transferDao, UserDao userDao, AccountDao accountDao){
        this.transferDao = transferDao;
        this.userDao = userDao;
        this.accountDao = accountDao;
    }
    @GetMapping
    public List<Transfer> getAllTransfers() {
        return transferDao.getAllTransfers();
    }
    @GetMapping("/user/{userId}")
    public List<Transfer> getTransfersByUserId(@PathVariable int userId) {
        return transferDao.getTransfersByUserId(userId);
    }
    @GetMapping("/transfers/{id}")
    public Transfer getTransferById(@PathVariable int id) {
        return transferDao.getTransferByTransferId(id);
    }
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{id}")
    public void createTransfer(@RequestBody Transfer transfer, @PathVariable int id){

    BigDecimal amountToTransfer = transfer.getTransferAmount();

    Account accountFrom = accountDao.getAccountByAccountID(transfer.getAccountFromId());
    Account accountTo = accountDao.getAccountByAccountID(transfer.getAccountToId());

    accountFrom.sendMoney(amountToTransfer);
    accountTo.receiveMoney(amountToTransfer);

    transferDao.createTransfer(transfer);

    accountDao.updateAccount(accountFrom);
    accountDao.updateAccount(accountTo);
    }

    @PutMapping("/{id}")
    public void updateTransferStatus(@RequestBody Transfer transfer, @PathVariable int id){
        if(transfer.getTransferStatusId() == 2 ) {

            BigDecimal amountToTransfer = transfer.getTransferAmount();
            Account accountFrom = accountDao.getAccountByAccountID(transfer.getAccountFromId());
            Account accountTo = accountDao.getAccountByAccountID(transfer.getAccountToId());

            accountFrom.sendMoney(amountToTransfer);
            accountTo.receiveMoney(amountToTransfer);

            transferDao.update(transfer);

            accountDao.updateAccount(accountFrom);
            accountDao.updateAccount(accountTo);
        } else {
            transferDao.update(transfer);
        }
    }






}
