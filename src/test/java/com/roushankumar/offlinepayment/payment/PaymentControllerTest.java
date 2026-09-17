package com.roushankumar.offlinepayment.payment;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.roushankumar.offlinepayment.account.entity.Account;
import com.roushankumar.offlinepayment.account.entity.AccountStatus;
import com.roushankumar.offlinepayment.account.repository.AccountRepository;
import com.roushankumar.offlinepayment.payment.dto.PaymentRequest;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PaymentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private AccountRepository accountRepository;

  @BeforeEach
  void setUp() {
    Account sender = new Account();
    sender.setAccountNumber("API-100001");
    sender.setHolderName("API Sender");
    sender.setBalance(new BigDecimal("1000.00"));
    sender.setCurrency("INR");
    sender.setStatus(AccountStatus.ACTIVE);

    Account receiver = new Account();
    receiver.setAccountNumber("API-100002");
    receiver.setHolderName("API Receiver");
    receiver.setBalance(new BigDecimal("500.00"));
    receiver.setCurrency("INR");
    receiver.setStatus(AccountStatus.ACTIVE);

    accountRepository.save(sender);
    accountRepository.save(receiver);
  }

  @Test
  void shouldCreatePayment() throws Exception {
    PaymentRequest request = new PaymentRequest(
        "API-100001",
        "API-100002",
        new BigDecimal("250.00"),
        "api-idem-001");

    mockMvc.perform(post("/api/payments")
        .with(csrf())
        .with(user("test-user"))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.transactionId").isNotEmpty())
        .andExpect(jsonPath("$.senderAccountNumber")
            .value("API-100001"))
        .andExpect(jsonPath("$.receiverAccountNumber")
            .value("API-100002"))
        .andExpect(jsonPath("$.amount")
            .value(250.00))
        .andExpect(jsonPath("$.currency")
            .value("INR"))
        .andExpect(jsonPath("$.status")
            .value("COMPLETED"));
  }

  @Test
  void shouldRejectInvalidRequest() throws Exception {
    PaymentRequest request = new PaymentRequest(
        "",
        "API-100002",
        BigDecimal.ZERO,
        "");

    mockMvc.perform(post("/api/payments")
        .with(csrf())
        .with(user("test-user"))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }
}