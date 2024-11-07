package com.fladx.walletflow.controller;

import com.fladx.walletflow.dto.UpdateWalletRequest;
import com.fladx.walletflow.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("wallet")
    public ResponseEntity<?> updateWallet(@RequestBody UpdateWalletRequest request){
        walletService.updateWallet(request.getWalletId(), request.getOperationType(), request.getAmount());
        return ResponseEntity.ok().build();
    }

    @GetMapping("wallets/{WALLET_UUID}")
    public ResponseEntity<?> getBalance(@PathVariable UUID WALLET_UUID){
        long balance = walletService.getBalance(WALLET_UUID);

        return ResponseEntity.ok().body(balance);
    }
}
