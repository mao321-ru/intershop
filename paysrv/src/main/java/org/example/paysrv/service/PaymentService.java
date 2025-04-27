package org.example.paysrv.service;

import org.example.paysrv.domain.Balance;
import org.example.paysrv.domain.Purchase;

public interface PaymentService {

    Balance getBalance(String accountId);

    boolean pay( Purchase purchase);

}
