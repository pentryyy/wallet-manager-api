package com.pentryyy.wallet_manager_api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.Method;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pentryyy.wallet_manager_api.enumeration.OperationType;
import com.pentryyy.wallet_manager_api.exception.InsufficientFundsException;
import com.pentryyy.wallet_manager_api.exception.WalletNotFoundException;
import com.pentryyy.wallet_manager_api.model.Wallet;
import com.pentryyy.wallet_manager_api.model.WalletRequest;
import com.pentryyy.wallet_manager_api.repository.WalletRepository;
import com.pentryyy.wallet_manager_api.service.WalletService;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(MockitoExtension.class)
class WalletControllerTest {

    @Mock
    private WalletService walletService;

    @InjectMocks
    private WalletController controller;

    private ObjectMapper objectMapper;
    private MockMvc      mockMvc;

    @BeforeEach
    void setup() {
        this.objectMapper = new ObjectMapper();
        this.mockMvc      = standaloneSetup(controller).build();
    }

      /**
     * Тест успешного обновления баланса
     */
    @Test
    void testUpdateBalanceSuccessWithResponseCheck() throws Exception {
        UUID          uuid          = UUID.randomUUID();
        OperationType operationType = OperationType.DEPOSIT;
        Double        amount        = 100.0;
        
        WalletRequest request = new WalletRequest(uuid, operationType, amount);
        
        mockMvc.perform(post("/api/v1/wallet")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andReturn();
    }

    /**
     * Тест исключения InsufficientFundsException 
     * при попытке списать больше баланса
     */
    @Test
    void testUpdateBalanceInsufficientFundsException() throws Exception {
        UUID          uuid          = UUID.randomUUID();
        OperationType operationType = OperationType.WITHDRAW;
        Double        amount        = 500.0;
        
        WalletRequest request = new WalletRequest(uuid, operationType, amount);
        
        doThrow(new InsufficientFundsException("Недостаточно средств"))
            .when(walletService)
            .updateBalance(eq(uuid), eq(operationType), eq(amount));
        
        mockMvc.perform(post("/api/v1/wallet")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest());
    }

    /**
     * Тест исключения WalletNotFoundException 
     * при попытке обновить баланс не существующего счета
     */
    @Test
    void testUpdateBalanceWalletNotFoundException() throws Exception {
        UUID          uuid          = UUID.randomUUID();
        OperationType operationType = OperationType.DEPOSIT;
        Double        amount        = 50.0;
        
        WalletRequest request = new WalletRequest(uuid, operationType, amount);
        
        doThrow(new WalletNotFoundException("Счет не найден"))
            .when(walletService)
            .updateBalance(eq(uuid), eq(operationType), eq(amount));
        
        mockMvc.perform(post("/api/v1/wallet")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isNotFound());
    }

     /**
     * Тест получения баланса
     * не существуюшего счета
     */
    @Test
    void testGetBalanceWalletNotFoundException() throws Exception {

        UUID walletId = UUID.randomUUID();
        doThrow(new WalletNotFoundException("Счет не найден")).when(walletService).getBalance(walletId);
        
        mockMvc.perform(get("/api/v1/wallets/" + walletId))
               .andExpect(status().isBadRequest());
    }

    /**
     * Тест отрицательного значения суммы при пополнении счета
     */
    @Test
    void testNegativeDepositAmount() throws Exception {
        UUID          uuid          = UUID.randomUUID();
        OperationType operationType = OperationType.DEPOSIT;
        Double        amount        = -100.0;
        
        WalletRequest request = new WalletRequest(uuid, operationType, amount);
        
        mockMvc.perform(post("/api/v1/wallet")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest());
    }

    /**
     * Тест  для проверки функциональности приватного метода withdraw 
     * в классе WalletService
     */
    @Test
    void testPrivateMethodWithdraw() throws Exception {
        WalletService walletService = new WalletService();

        WalletRepository walletRepository = Mockito.mock(WalletRepository.class);

        ReflectionTestUtils.setField(walletService, "walletRepository", walletRepository);

        Method withdrawMethod = WalletService.class.getDeclaredMethod("withdraw", Wallet.class, double.class);
        withdrawMethod.setAccessible(true);

        Wallet wallet = new Wallet();
        wallet.setBalance(500.0);

        withdrawMethod.invoke(walletService, wallet, 200.0);

        assertEquals(300.0, wallet.getBalance());
    }

     /**
     * Тест исключения NullPointerException 
     * при обновлении баланса с суммой null 
     */
    @Test
    @SuppressWarnings("null")
    void testUpdateBalanceNullPointerException() throws Exception {
        UUID          uuid          = UUID.randomUUID();
        OperationType operationType = OperationType.WITHDRAW;
        Double        amount        = null;
        
        WalletRequest request = new WalletRequest(uuid, operationType, amount);
        
        assertThrows(NullPointerException.class, () -> walletService.updateBalance(uuid, operationType, amount));

        mockMvc.perform(post("/api/v1/wallet")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Тест неверного типа операции
     */
    @Test
    void testInvalidOperationType() throws Exception {
        UUID          uuid          = UUID.randomUUID();
        OperationType operationType = null;
        Double        amount        = 100.0;
        
        WalletRequest request = new WalletRequest(uuid, operationType, amount);
        
        mockMvc.perform(post("/api/v1/wallet")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest());
    }
}
