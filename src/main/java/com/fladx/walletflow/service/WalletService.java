package com.fladx.walletflow.service;

import com.fladx.walletflow.configuration.Messages;
import com.fladx.walletflow.enums.OperationType;
import com.fladx.walletflow.model.Wallet;
import com.fladx.walletflow.repository.WalletRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class WalletService {
    private final WalletRepository walletRepository;
    private final EntityManager entityManager;

    public WalletService(WalletRepository walletRepository, EntityManager entityManager) {
        this.walletRepository = walletRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public void updateWallet(UUID walletId, OperationType operationType, long amount) {
        Wallet wallet = entityManager.find(Wallet.class, walletId, LockModeType.PESSIMISTIC_WRITE);

        if (wallet == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, Messages.WALLET_NOT_FOUND.formatted(walletId));

        switch (operationType) {
            case DEPOSIT -> {
                wallet.setBalance(wallet.getBalance() + amount);
            }
            case WITHDRAW -> {
                long newBalance = wallet.getBalance() - amount;
                if (newBalance < 0)
                    throw new ResponseStatusException(HttpStatus.CONFLICT, Messages.INSUFFICIENT_FUNDS);

                wallet.setBalance(newBalance);
            }
        }

        walletRepository.save(wallet);
    }

    public long getBalance(UUID walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, Messages.WALLET_NOT_FOUND.formatted(walletId)));

        return wallet.getBalance();
    }
}
