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

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/v1")
public class WalletController {

    @Autowired
    private  WalletService walletService;
    
    private final ErrorResponseBuilder builder = new ErrorResponseBuilder();

    @PostMapping("/wallet")
    public ResponseEntity<Map<String, Object>> updateBalance(@RequestBody WalletRequest request) {
        
        if (request.getOperationType() == null) {
            return new ResponseEntity<>(builder.createErrorResponse("Операция не должна быть null"), HttpStatus.BAD_REQUEST);
        }

        Double amount = request.getAmount();
        if (amount == null || amount < 0) {
            return new ResponseEntity<>(builder.createErrorResponse("Значение суммы должно быть больше или равно 0"), HttpStatus.BAD_REQUEST);
        }

        try {
            walletService.updateBalance(request.getWalletId(), request.getOperationType(), request.getAmount());
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