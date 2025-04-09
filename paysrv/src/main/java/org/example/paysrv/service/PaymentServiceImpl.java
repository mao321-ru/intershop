package org.example.paysrv.service;

import org.example.paysrv.domain.Balance;
import org.example.paysrv.domain.Purchase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.BitSet;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final ReentrantLock lock = new ReentrantLock();

    private BigDecimal balance = BigDecimal.ZERO;

    PaymentServiceImpl( @Value("${paysrv.init.balance}") String initBalance) {
        balance = new BigDecimal( initBalance);
    }

    @Override
    public Balance getBalance() {
        return new Balance( balance);
    }

    @Override
    public boolean pay( Purchase purchase) {
        boolean isOk = false;
        BigDecimal amount = purchase.getAmount();
        // неотрицательное и точность не более 2-х знаков после запятой
        if( amount.signum() >= 0 && amount.movePointRight(2).remainder( BigDecimal.ONE).signum() == 0) {
            lock.lock();
            try {
                BigDecimal newBalance = balance.subtract( amount);
                if ( newBalance.signum() >= 0) {
                    balance = newBalance;
                    isOk = true;
                }
            }
            finally {
                lock.unlock();
            }
        }
        return isOk;
    }

}
