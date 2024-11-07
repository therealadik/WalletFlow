package com.fladx.walletflow.service;

import com.fladx.walletflow.configuration.Messages;
import com.fladx.walletflow.enums.OperationType;
import com.fladx.walletflow.model.Wallet;
import com.fladx.walletflow.repository.WalletRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private WalletService walletService;

    private UUID walletId;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        walletId = UUID.randomUUID();
        wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setBalance(1000);
    }

    @Test
    void testDepositSuccess() {
        when(entityManager.find(Wallet.class, walletId, LockModeType.PESSIMISTIC_WRITE)).thenReturn(wallet);

        walletService.updateWallet(walletId, OperationType.DEPOSIT, 500);

        assertEquals(1500, wallet.getBalance());
        verify(walletRepository).save(wallet);
    }

    @Test
    void testWithdrawSuccess() {
        when(entityManager.find(Wallet.class, walletId, LockModeType.PESSIMISTIC_WRITE)).thenReturn(wallet);

        walletService.updateWallet(walletId, OperationType.WITHDRAW, 500);

        assertEquals(500, wallet.getBalance());
        verify(walletRepository).save(wallet);
    }

    @Test
    void testWithdrawInsufficientFunds() {
        when(entityManager.find(Wallet.class, walletId, LockModeType.PESSIMISTIC_WRITE)).thenReturn(wallet);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                walletService.updateWallet(walletId, OperationType.WITHDRAW, 1500));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals(Messages.INSUFFICIENT_FUNDS, exception.getReason());
    }

    @Test
    void testGetBalanceExistingWallet() {
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        long balance = walletService.getBalance(walletId);
        assertEquals(1000, balance);
    }

    @Test
    void testGetBalanceNonExistingWallet() {
        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                walletService.getBalance(walletId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals(Messages.WALLET_NOT_FOUND.formatted(walletId), exception.getReason());
    }
}