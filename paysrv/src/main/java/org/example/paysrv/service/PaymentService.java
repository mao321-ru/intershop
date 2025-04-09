package org.example.paysrv.service;

import org.example.paysrv.domain.Balance;
import org.example.paysrv.domain.Purchase;

import java.math.BigDecimal;

public interface PaymentService {

    Balance getBalance();

    boolean pay( Purchase purchase);

}
