package org.example.paysrv.controller;

import org.example.paysrv.IntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.reactive.server.WebTestClient;

// Общие настройки и т.д. для интеграционныйх тестов контроллеров
public class ControllerTest extends IntegrationTest {

    @Value( "${paysrv.init.balance}")
    protected String INIT_BALANCE;

    @Autowired
    WebTestClient wtc;
}
