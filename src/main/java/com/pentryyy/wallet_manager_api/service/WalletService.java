package com.pentryyy.wallet_manager_api.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pentryyy.wallet_manager_api.enumeration.OperationType;
import com.pentryyy.wallet_manager_api.exception.InsufficientFundsException;
import com.pentryyy.wallet_manager_api.exception.WalletNotFoundException;
import com.pentryyy.wallet_manager_api.model.Wallet;
import com.pentryyy.wallet_manager_api.repository.WalletRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WalletService {

    @Autowired
    private final WalletRepository walletRepository;

    private Wallet getWallet(UUID walletId) {
        return walletRepository.findByWalletId(walletId)
                               .orElseThrow(() -> new WalletNotFoundException("Счет не найден"));
    }

    private void deposit(Wallet wallet, double amount) {
        wallet.setBalance(wallet.getBalance() + amount);
        walletRepository.save(wallet);
    }

    private void withdraw(Wallet wallet, double amount) {
        if (wallet.getBalance() < amount) {
            throw new InsufficientFundsException("Недостаточно средств");
        }
        wallet.setBalance(wallet.getBalance() - amount);
        walletRepository.save(wallet);
    }

    @Transactional
    public void updateBalance(UUID walletId, OperationType operationType, double amount) {
        Wallet wallet = getWallet(walletId);

        if (OperationType.DEPOSIT.equals(operationType)) {
            deposit(wallet, amount);
        } else if (OperationType.WITHDRAW.equals(operationType)) {
            withdraw(wallet, amount);
        }
    }

    @Transactional(readOnly = true)
    public double getBalance(UUID walletId) {
        return getWallet(walletId).getBalance();
    }
}
