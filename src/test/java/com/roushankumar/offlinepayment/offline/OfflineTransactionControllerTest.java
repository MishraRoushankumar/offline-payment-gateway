package com.roushankumar.offlinepayment.offline;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.roushankumar.offlinepayment.account.entity.Account;
import com.roushankumar.offlinepayment.account.repository.AccountRepository;
import com.roushankumar.offlinepayment.offline.dto.OfflineTransactionRequest;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OfflineTransactionControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private AccountRepository accountRepository;

  @BeforeEach
  void setUp() {
    Account sender = new Account();
    sender.setAccountNumber("OFF-100001");
    sender.setHolderName("Offline Sender");
    sender.setBalance(new BigDecimal("5000.00"));
    sender.setCurrency("INR");

    Account receiver = new Account();
    receiver.setAccountNumber("OFF-100002");
    receiver.setHolderName("Offline Receiver");
    receiver.setBalance(new BigDecimal("1000.00"));
    receiver.setCurrency("INR");

    accountRepository.save(sender);
    accountRepository.save(receiver);
  }

  @Test
  @WithMockUser
  void shouldCreateOfflineTransaction() throws Exception {
    OfflineTransactionRequest request = new OfflineTransactionRequest(
        "OFF-100001",
        "OFF-100002",
        new BigDecimal("250.00"));

    mockMvc.perform(post("/api/offline-transactions")
        .with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.offlineTransactionId").isNotEmpty())
        .andExpect(jsonPath("$.senderAccountNumber")
            .value("OFF-100001"))
        .andExpect(jsonPath("$.receiverAccountNumber")
            .value("OFF-100002"))
        .andExpect(jsonPath("$.amount")
            .value(250.00))
        .andExpect(jsonPath("$.currency")
            .value("INR"))
        .andExpect(jsonPath("$.status")
            .value("CREATED"))
        .andExpect(jsonPath("$.createdAt").isNotEmpty());
  }

  @Test
  @WithMockUser
  void shouldRejectInvalidOfflineTransactionRequest() throws Exception {
    OfflineTransactionRequest request = new OfflineTransactionRequest(
        "",
        "OFF-100002",
        BigDecimal.ZERO);

    mockMvc.perform(post("/api/offline-transactions")
        .with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }
}