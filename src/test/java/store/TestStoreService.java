package store;

import apiModels.Order;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.time.OffsetDateTime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@Epic("Тестированрие Api endpoints для работы с заказами")
public class TestStoreService {

    static Order memOrder;

    @BeforeAll
    public static void setUpBeforeClass() throws Exception {
        RestAssured.baseURI = "https://petstore.swagger.io/v2/store";

    }

    @BeforeEach
    public void createOrder() throws Exception {
        Order o = new Order(2, 22, 3, OffsetDateTime.now(), "placed", true);
        given().header("Content-Type", "application/json").header("Accept", "application/json")
                .body(o)
                .log().ifValidationFails()
                .when().post("/order")
                .then().statusCode(200);
        memOrder = o;
    }

    @Test
    @DisplayName("Тестировние создание заказа на питомца")
    @Description("Тестировние создание заказа на питомца")
    @Step("Создание заказа на питомца")
    public void testSuccessCreateOrder(){
        Order o = new Order(1, 21, 3, OffsetDateTime.now(), "placed", true);
        given().header("Content-Type", "application/json").header("Accept", "application/json")
                .body(o)
                .log().ifValidationFails()
                .when().post("/order")
                .then().statusCode(200);
    }

    @Test
    @DisplayName("Тестировние создание заказа на питомца без данных")
    @Description("Тестировние создание заказа на питомца без данных ")
    @Step("Создание заказа на питомца")
    public void testCreateOrderWithNoData(){
        Order o = new Order();
        given().header("Content-Type", "application/json").header("Accept", "application/json")
                .body(o)
                .log().ifValidationFails()
                .when().post("/order")
                .then().statusCode(200);
    }

    @Test
    @DisplayName("Тестировние создание заказа на питомца без указанрия статуса")
    @Description("Тестировние создание заказа на питомца без указанрия статуса")
    @Step("Создание заказа на питомца")
    public void testSuccessCreateOrdert(){
        Order o = new Order(1, 21, 3, OffsetDateTime.now(), "", true);
        given().header("Content-Type", "application/json").header("Accept", "application/json")
                .body(o)
                .log().ifValidationFails()
                .when().post("/order")
                .then().statusCode(200);
    }

    @Test
    @DisplayName("Тестировние создание заказа на питомца без указания application/json")
    @Description("Тестировние создание заказа на питомца без указания application/json")
    @Step("Создание заказа на питомца")
    public void testCreateOrderWithoutHeader(){
        Order o = new Order(1, 21, 3, OffsetDateTime.now(), "placed", true);
        given()
                .body(o)
                .log().ifValidationFails()
                .when().post("/order")
                .then().statusCode(415);
    }

    @Test
    @DisplayName("Тестировние получение заказа по id")
    @Description("Тестировние получение заказа по id")
    @Step("Получение заказа по id")
    public void testSuccessGetOrderById(){
        given().header("Accept", "application/json")
                .when().get("/order/"+memOrder.getId())
                .then().statusCode(200)
                .body("id", equalTo(memOrder.getId()))
                .body("petId", equalTo(memOrder.getPetId()))
                .body("quantity", equalTo(memOrder.getQuantity()))
                .body("status", equalTo(memOrder.getStatus()))
                .body("complete", equalTo(memOrder.getComplete()));


    }

    @Test
    @DisplayName("Тестировние получение заказа которого нет по id ")
    @Description("Тестировние получение заказа по id")
    @Step("Получение несуществующего заказа по id")
    public void testFailedGetOrderById(){
        given().header("Accept", "application/json")
                .when().get("/order/"+0)
                .then().statusCode(404);

    }

    @Test
    @DisplayName("Тестировние удаления заказа  по id ")
    @Description("Тестировние удаления заказа по id")
    @Step("Удаление заказа по id")
    public void testSuccessDeleteOrderById(){
        given().header("Accept", "application/json")
                .when().delete("/order/"+memOrder.getId())
                .then().statusCode(200);

    }

    @Test
    @DisplayName("Тестировние удаления несуществующего заказа  по id ")
    @Description("Тестировние удаления несуществующего заказа по id")
    @Step("Удаление несуществующего заказа по id")
    public void  testFailedDeleteOrderById(){
        given().header("Accept", "application/json")
                .when().delete("/order/"+0)
                .then().statusCode(404);

    }

    @Test
    @DisplayName("Тестировние получение количества питомцев по статусу")
    @Description("Тестировние получение количества питомцев по статусу")
    @Step("Инвенторизация питомцев по статусу")
    public void testGetInventory(){
        given().header("Accept", "application/json")
                .when().get("/inventory")
                .then().statusCode(200);

    }
}
