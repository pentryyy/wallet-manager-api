package com.pentryyy.wallet_manager_api.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pentryyy.wallet_manager_api.model.Wallet;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Integer> {

    Optional<Wallet> findByWalletId(UUID walletId);
}
