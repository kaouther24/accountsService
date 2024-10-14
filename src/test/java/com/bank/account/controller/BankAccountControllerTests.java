package com.bank.account.controller;

import com.bank.account.service.BankAccountService;
        import com.bank.account.model.BankAccount;
        import org.junit.jupiter.api.BeforeEach;
        import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
        import org.springframework.boot.test.mock.mockito.MockBean;
        import org.springframework.http.MediaType;
        import org.springframework.test.web.servlet.MockMvc;
        import org.springframework.test.web.servlet.ResultActions;

        import java.math.BigDecimal;
        import java.util.Arrays;
        import java.util.Optional;

        import static org.mockito.ArgumentMatchers.any;
        import static org.mockito.Mockito.when;
        import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
        import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BankAccountController.class)
public class BankAccountControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BankAccountService accountService;

    private BankAccount account1;
    private BankAccount account2;

    @BeforeEach
    void setUp() {
        account1 = new BankAccount("123", "Customer1", new BigDecimal(1000));
        account2 = new BankAccount("124", "Customer2", new BigDecimal(1500));
    }

    @Test
    void testHealthCheck() throws Exception {
        mockMvc.perform(get("/bankAccount/healthCheck"))
                .andExpect(status().isOk())
                .andExpect(content().string("Application is healthy"));
    }

    @Test
    void testGetAccountByUuid() throws Exception {
        // Mocking the service response
        when(accountService.findBankAccountByUuid("123")).thenReturn(Optional.of(account1));

        mockMvc.perform(get("/bankAccount/byIdAccount/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value("123"))
                .andExpect(jsonPath("$.customerUuid").value("Customer1"))
                .andExpect(jsonPath("$.balance").value(1000));
    }

    @Test
    void testGetAccountByCustomerUuid() throws Exception {
        // Mocking the service response
        when(accountService.findBankAccountsByCustomerUuid("Customer1")).thenReturn(Arrays.asList(account1));

        mockMvc.perform(get("/bankAccount/byCustomerId/Customer1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].uuid").value("123"))
                .andExpect(jsonPath("$[0].customerUuid").value("Customer1"))
                .andExpect(jsonPath("$[0].balance").value(1000));
    }

    @Test
    void testCreateNewAccount() throws Exception {
        // Mocking the service response for adding a new account
        when(accountService.addNewAccount(any(BankAccount.class))).thenReturn(Arrays.asList(account1, account2));

        String newAccountJson = "{\"customerUuid\":\"Customer1\",\"balance\":1000}";

        ResultActions resultActions = mockMvc.perform(post("/bankAccount/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newAccountJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].uuid").value("123"))
                .andExpect(jsonPath("$[0].customerUuid").value("Customer1"))
                .andExpect(jsonPath("$[0].balance").value(1000))
                .andExpect(jsonPath("$[1].uuid").value("124"))
                .andExpect(jsonPath("$[1].customerUuid").value("Customer2"))
                .andExpect(jsonPath("$[1].balance").value(1500));
    }
}