package com.picpaysimplificado.service;


import com.picpaysimplificado.domain.transaction.Transaction;
import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.dto.TransactionDTO;
import com.picpaysimplificado.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class TransactionService {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private NotificationService notificationService;


    public Transaction createTransaction(TransactionDTO transactionDTO) throws Exception {

        User sender = userService.findUserById(transactionDTO.senderId());
        User receiver = userService.findUserById(transactionDTO.receiverId());

        userService.validateTransaction(sender, transactionDTO.value());

        boolean isAuthorized = authorizeTransaction(sender, transactionDTO.value());
        if (!isAuthorized) {
            throw new Exception("Transação não autorizada!");
        }

        Transaction transaction = new Transaction();
        transaction.setAmount(transactionDTO.value());
        transaction.setSender(transaction.getSender());
        transaction.setReceiver(transaction.getReceiver());
        transaction.setTimeStamp(LocalDateTime.now());

        sender.setBalance(sender.getBalance().subtract(transactionDTO.value()));
        receiver.setBalance(receiver.getBalance().add(transactionDTO.value()));

        transactionRepository.save(transaction);
        userService.saveUser(sender);
        userService.saveUser(receiver);

        notificationService.senderNotification(sender,"Transação enviada com sucesso!");
        notificationService.senderNotification(receiver,"Transação recebida com sucesso!");

        return transaction;
    }

    public boolean authorizeTransaction(User sender, BigDecimal value) {

        ResponseEntity<Map> authorizationResponse = restTemplate.getForEntity(
                "https://util.devi.tools/api/v2/authorize",
                Map.class);

        if (authorizationResponse.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> body = authorizationResponse.getBody();
            if (body != null && "success".equals(body.get("status"))) {
                Map<String, Object> data = (Map<String, Object>) body.get("data");
                if (data != null) {
                    Object auth = data.get("authorization");
                    return Boolean.TRUE.equals(auth);
                }
            }
        }
        return false;
    }
}
