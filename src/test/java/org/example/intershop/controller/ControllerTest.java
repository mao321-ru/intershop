package org.example.intershop.controller;

import jakarta.persistence.EntityManager;
import org.example.intershop.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

// Общие настройки и т.д. для интеграционныйх тестов контроллеров
public class ControllerTest extends IntegrationTest {

    // Стартвый Id последовательностей для временных (создаваемых в тесте) данных
    protected final long TEMP_DATA_START_ID = 1001L;

    // Тестовые данные: Id товара (с изображением)
    protected final long EXISTS_PRODUCT_ID = 1L;
    protected final String EXISTS_PRODUCT_NAME = "Шампунь SUPER";
    protected final BigDecimal EXISTS_PRODUCT_PRICE = new BigDecimal( "10.81");

    // Тестовые данные: Id товара без изображения
    protected final long NO_IMAGE_PRODUCT_ID = 2L;

    // Id непродававшегося товара (для проверки удаления)
    protected final long UNSELLABLE_PRODUCT_ID = 5L;

    // выбор всех элементов с товарами
    protected final String PRODUCTS_XPATH = "//*[@class=\"product\"]";

    // выбор элемента для поля товара, например PR_FIELD_XPF.formatted( "productName")
    protected final String PR_FIELD_XPF = PRODUCTS_XPATH + "//*[@class=\"product__%s\"]";

    // выбор элемента с указанным значением/текстом/src поля товара, например PR_VAL_XPF.formatted( "productName", "Мыло DURU")
    protected final String PR_VAL_XPF = PR_FIELD_XPF + "[@value=\"%s\"]";
    protected final String PR_TEXT_XPF = PR_FIELD_XPF + "[text()=\"%s\"]";
    protected final String PR_SRC_XPF = PR_FIELD_XPF + "[@src=\"%s\"]";

    protected final String TOTAL_XPATH = "//*[@class=\"total\"]";
    protected final String TOTAL_TEXT_XPF = TOTAL_XPATH + "[text()=\"%s\"]";

    // Тестовые данные: Id заказа
    protected final long EXISTS_ORDER_ID = 1L;
    protected final int EXISTS_ORDER_PRODUCT_COUNT = 2;
    protected final BigDecimal EXISTS_ORDER_TOTAL = new BigDecimal( "25.02");

    // выбор всех элементов с заказами
    protected final String ORDERS_XPATH = "//*[@class=\"order\"]";

    protected final String ORDERS_TOTAL_XPATH = "//*[@class=\"orders__total\"]";


    @Autowired
    EntityManager em;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    protected MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup( webApplicationContext).build();
    }

}
