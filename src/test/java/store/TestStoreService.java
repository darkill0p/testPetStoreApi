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

@Epic("������������� Api endpoints ��� ������ � ��������")
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
    @DisplayName("����������� �������� ������ �� �������")
    @Description("����������� �������� ������ �� �������")
    @Step("�������� ������ �� �������")
    public void testSuccessCreateOrder(){
        Order o = new Order(1, 21, 3, OffsetDateTime.now(), "placed", true);
        given().header("Content-Type", "application/json").header("Accept", "application/json")
                .body(o)
                .log().ifValidationFails()
                .when().post("/order")
                .then().statusCode(200);
    }

    @Test
    @DisplayName("����������� �������� ������ �� ������� ��� ������")
    @Description("����������� �������� ������ �� ������� ��� ������ ")
    @Step("�������� ������ �� �������")
    public void testCreateOrderWithNoData(){
        Order o = new Order();
        given().header("Content-Type", "application/json").header("Accept", "application/json")
                .body(o)
                .log().ifValidationFails()
                .when().post("/order")
                .then().statusCode(200);
    }

    @Test
    @DisplayName("����������� �������� ������ �� ������� ��� ��������� �������")
    @Description("����������� �������� ������ �� ������� ��� ��������� �������")
    @Step("�������� ������ �� �������")
    public void testSuccessCreateOrdert(){
        Order o = new Order(1, 21, 3, OffsetDateTime.now(), "", true);
        given().header("Content-Type", "application/json").header("Accept", "application/json")
                .body(o)
                .log().ifValidationFails()
                .when().post("/order")
                .then().statusCode(200);
    }

    @Test
    @DisplayName("����������� �������� ������ �� ������� ��� �������� application/json")
    @Description("����������� �������� ������ �� ������� ��� �������� application/json")
    @Step("�������� ������ �� �������")
    public void testCreateOrderWithoutHeader(){
        Order o = new Order(1, 21, 3, OffsetDateTime.now(), "placed", true);
        given()
                .body(o)
                .log().ifValidationFails()
                .when().post("/order")
                .then().statusCode(415);
    }

    @Test
    @DisplayName("����������� ��������� ������ �� id")
    @Description("����������� ��������� ������ �� id")
    @Step("��������� ������ �� id")
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
    @DisplayName("����������� ��������� ������ �������� ��� �� id ")
    @Description("����������� ��������� ������ �� id")
    @Step("��������� ��������������� ������ �� id")
    public void testFailedGetOrderById(){
        given().header("Accept", "application/json")
                .when().get("/order/"+0)
                .then().statusCode(404);

    }

    @Test
    @DisplayName("����������� �������� ������  �� id ")
    @Description("����������� �������� ������ �� id")
    @Step("�������� ������ �� id")
    public void testSuccessDeleteOrderById(){
        given().header("Accept", "application/json")
                .when().delete("/order/"+memOrder.getId())
                .then().statusCode(200);

    }

    @Test
    @DisplayName("����������� �������� ��������������� ������  �� id ")
    @Description("����������� �������� ��������������� ������ �� id")
    @Step("�������� ��������������� ������ �� id")
    public void  testFailedDeleteOrderById(){
        given().header("Accept", "application/json")
                .when().delete("/order/"+0)
                .then().statusCode(404);

    }

    @Test
    @DisplayName("����������� ��������� ���������� �������� �� �������")
    @Description("����������� ��������� ���������� �������� �� �������")
    @Step("�������������� �������� �� �������")
    public void testGetInventory(){
        given().header("Accept", "application/json")
                .when().get("/inventory")
                .then().statusCode(200);

    }
}
