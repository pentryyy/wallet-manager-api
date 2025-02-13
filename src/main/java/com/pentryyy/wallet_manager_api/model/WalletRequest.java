package com.pentryyy.wallet_manager_api.model;

import java.util.UUID;

import com.pentryyy.wallet_manager_api.enumeration.OperationType;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletRequest {

    private UUID          walletId;
    private OperationType operationType;

    @NotNull
    private Double amount;
}