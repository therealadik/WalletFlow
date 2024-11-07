package com.fladx.walletflow.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class Wallet {
    @Id
    private UUID id;

    private long balance = 0;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }
}