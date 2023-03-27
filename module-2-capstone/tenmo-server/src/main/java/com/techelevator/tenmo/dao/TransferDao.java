package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {
    List<Transfer> getAllTransfers();

    Transfer getTransferByTransferId(int transferId);

    void createTransfer(Transfer transfer);
    void update(Transfer transfer);

    List<Transfer> getTransfersByUserId(int userId);
}
