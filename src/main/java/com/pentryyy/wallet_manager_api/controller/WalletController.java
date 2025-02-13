package com.pentryyy.wallet_manager_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pentryyy.wallet_manager_api.exception.ErrorResponseBuilder;
import com.pentryyy.wallet_manager_api.exception.InsufficientFundsException;
import com.pentryyy.wallet_manager_api.exception.WalletNotFoundException;
import com.pentryyy.wallet_manager_api.model.WalletRequest;
import com.pentryyy.wallet_manager_api.service.WalletService;

import jakarta.validation.Valid;

import java.util.Map;
import java.util.UUID;

import org.aspectj.lang.annotation.Around;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class WalletController {

    @Autowired
    private final WalletService walletService;
    
    private final ErrorResponseBuilder builder = new ErrorResponseBuilder();

    @PostMapping("/wallet")
    @Around("postMappingWithPath('/wallet')")
    public ResponseEntity<Map<String, Object>> updateBalance(@Valid @RequestBody WalletRequest request) {
        try {
            walletService.updateBalance(request.getWalletId(), request.getOperationType(), Math.abs(request.getAmount()));
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (WalletNotFoundException e) {
            return new ResponseEntity<>(builder.createErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (InsufficientFundsException e) {
            return new ResponseEntity<>(builder.createErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (NullPointerException e) {
            return new ResponseEntity<>(builder.createErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/wallets/{walletId}")
    public ResponseEntity<?> getBalance(@PathVariable UUID walletId) {
        try {
            double balance = walletService.getBalance(walletId);
            return new ResponseEntity<>(balance, HttpStatus.OK);
        } catch (WalletNotFoundException e) {
            return new ResponseEntity<>(builder.createErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}