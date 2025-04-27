package org.example.paysrv.service;

import lombok.extern.slf4j.Slf4j;
import org.example.paysrv.domain.Balance;
import org.example.paysrv.domain.Purchase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private BigDecimal initBalance;

    ConcurrentHashMap<String,BigDecimal> accountBalance = new ConcurrentHashMap<>();

    PaymentServiceImpl( @Value("${paysrv.init.balance}") String initBalance) {
        this.initBalance = new BigDecimal( initBalance);
    }

    private BigDecimal getAccountBalance( String accountId) {
        return accountBalance.getOrDefault( accountId, initBalance);
    }

    @Override
    public Balance getBalance(String accountId) {
        return new Balance( getAccountBalance( accountId));
    }

    @Override
    public boolean pay( Purchase purchase) {
        boolean isOk = false;
        BigDecimal amount = purchase.getAmount();
        // неотрицательное и точность не более 2-х знаков после запятой
        if( amount.signum() >= 0 && amount.movePointRight(2).remainder( BigDecimal.ONE).signum() == 0) {
            String accountId = purchase.getAccountId();
            // предварительная проверка для уменьшения вероятности ошибки из-за нехватки средств при выполнении платежа
            if( amount.compareTo( getAccountBalance( accountId)) <= 0) {
                try {
                    accountBalance.merge(accountId, initBalance.subtract(amount), (oldBalance, defaultBalance) -> {
                        BigDecimal newBalance = oldBalance.subtract(amount);
                        if (newBalance.signum() < 0) throw new IllegalStateException("Not enough money");
                        return newBalance;
                    });
                    isOk = true;
                }
                catch( IllegalStateException e) {
                    log.debug( "no money: {}", e.toString());
                }
            }
        }
        return isOk;
    }

}
