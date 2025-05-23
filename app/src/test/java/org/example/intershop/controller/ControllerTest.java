package org.example.intershop.controller;

import org.example.intershop.IntegrationTest;

import org.example.intershop.model.CartProduct;
import org.example.intershop.model.Image;
import org.example.intershop.model.Order;
import org.example.intershop.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

// Общие настройки и т.д. для интеграционныйх тестов контроллеров
public class ControllerTest extends IntegrationTest {

    // Чиспо товаров в тестовых данных (src/test/resources/test-data.sql)
    protected final int PRODUCTS_COUNT = 5;

    // Неиспользуемое значение Id
    protected final long NOT_EXISTS_DATA_ID = 999L;

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

    // Id и количество товара в корзине у пользователя user3
    protected final long USER3_IN_CART_PRODUCT_ID = 2L;
    protected final int USER3_IN_CART_PRODUCT_QTY = 3;

    // элемент для перехода на следующую страницу
    protected final String NEXT_PAGE_XPATH = "//*[@class=\"form__nextPage\"]";

    // ссылки на login и logoff
    protected final String LOGIN_LINK_XPATH = "//a[@href=\"/login\"]";
    protected final String LOGOUT_LINK_XPATH = "//a[@href=\"/logout\"]";

    // ссылка на Корзину
    protected final String CART_LINK_XPATH = "//a[@href=\"/cart\"]";

    // ссылка на Заказы
    protected final String ORDERS_LINK_XPATH = "//a[@href=\"/orders\"]";

    // выбор всех элементов с товарами
    protected final String PRODUCTS_XPATH = "//*[@class=\"product\"]";

    // выбор элемента для поля товара, например PR_FIELD_XPF.formatted( "productName")
    protected final String PR_FIELD_XPF = PRODUCTS_XPATH + "//*[@class=\"product__%s\"]";

    // выбор элемента с указанным значением/текстом/src поля товара, например PR_VAL_XPF.formatted( "productName", "Мыло DURU")

    // выбор элементов изменения количества товара в корзине
    protected final String PRODUCT_IN_CART_XPATH = PR_FIELD_XPF.formatted( "inCart");

    // выбор элементов с количеством товара в корзине
    protected final String PRODUCT_IN_CART_QTY_XPATH = PR_FIELD_XPF.formatted( "inCartQuantity");

    // выбор элементов с количеством товара в корзине отличным от 0
    protected final String PRODUCT_IN_CART_QTY_EXISTS_XPATH = PRODUCT_IN_CART_QTY_XPATH + "[text() != '' and text() != '0']";

    // выбор элемента с указанным значением/текстом/src поля товара, например PR_VAL_XPF.formatted( "productName", "Мыло DURU")
    protected final String PR_VAL_XPF = PR_FIELD_XPF + "[@value=\"%s\"]";
    protected final String PR_TEXT_XPF = PR_FIELD_XPF + "[text()=\"%s\"]";
    protected final String PR_SRC_XPF = PR_FIELD_XPF + "[@src=\"%s\"]";

    protected final String TOTAL_XPATH = "//*[@class=\"total\"]";
    protected final String TOTAL_TEXT_XPF = TOTAL_XPATH + "[text()=\"%s\"]";

    // Тестовые данные: Id существующего заказа у пользователя user
    protected final long USER_EXISTS_ORDER_ID = 1L;
    protected final int EXISTS_ORDER_PRODUCT_COUNT = 2;
    protected final BigDecimal EXISTS_ORDER_TOTAL = new BigDecimal( "25.02");

    // выбор всех элементов с заказами
    protected final String ORDERS_XPATH = "//*[@class=\"order\"]";

    protected final String ORDERS_TOTAL_XPATH = "//*[@class=\"orders__total\"]";

    @Autowired
    R2dbcEntityTemplate etm;

    @Autowired
    WebTestClient wtc;

    protected Product getProductById(Long productId) {
        return etm.selectOne( query( where( "id").is( productId)), Product.class).block();
    }

    protected Image getImageById(Long imageId) {
        return etm.selectOne( query( where( "id").is( imageId)), Image.class).block();
    }

    protected CartProduct getCartProductByProductId(Long productId) {
        return etm.selectOne( query( where( "product_id").is( productId)), CartProduct.class).block();
    }

    protected Order getOrderById(Long orderId) {
        return etm.selectOne( query( where( "id").is( orderId)), Order.class).block();
    }

}
