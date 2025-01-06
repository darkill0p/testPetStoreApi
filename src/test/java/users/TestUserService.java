package users;

import apiModels.UserSchema;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;


@Epic("������������ API endpoints ��� ������������� (Users)")
public class TestUserService {

    UserSchema createdUser;

    @BeforeAll
    static void init() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2/user";
    }

    @BeforeEach
    void createUser() {
        if (createdUser == null) {
            createdUser = new UserSchema(1, "whiteUser", "ivanov", "ivanovich", "ivanov@mail.ru", "12345678", "899999999999", 1);
            given().header("Content-Type", "application/json").header("Accept", "application/json")
                    .body(createdUser)
                    .when().post()
                    .then().statusCode(200);
        }
    }

    @Test
    @DisplayName("������������ �������� ����������� ������������")
    @Description("���� ���� ��������� �������� ����������� ������ ������������ � �������")
    @Severity(SeverityLevel.NORMAL)
    @Story("����������� ������ ������������")
    public void testSuccessCreateUser() {
        UserSchema user = new UserSchema(1, "dd1", "���������", "���������", "ivanov@mail.ru", "12345678", "899999999999", 1);
        given().header("Content-Type", "application/json").header("Accept", "application/json")
                .body(user)
                .when().post()
                .then().statusCode(200);
    }

    @Test
    @DisplayName("������������  ����������� ������������ ��� �������� header application/json")
    @Description("���� ���� ���������  ����������� ������ ������������ � �������")
    @Severity(SeverityLevel.NORMAL)
    @Story("����������� ������ ������������ ��� �������� header application/json")
    public void testSuccess1CreateUser() {
        UserSchema user = new UserSchema(1, "dd1", "���������", "���������", "ivanov@mail.ru", "12345678", "899999999999", 1);
        given().body(user)
                .when().post()
                .then().statusCode(415);
    }

    @Test
    @DisplayName("������������  ����������� ������������ ��� �������� ������")
    @Description("���� ���� ���������  ����������� ������ ������������ � ������� ��� �������� ������")
    @Severity(SeverityLevel.NORMAL)
    @Story("����������� ������ ������������")
    public void testCreateUserWithNullData() {
        UserSchema user = new UserSchema();
        given().header("Content-Type", "application/json").header("Accept", "application/json")
                .body(user)
                .when().post()
                .then().statusCode(200);
    }


    @Test
    @DisplayName("������������ ��������� ��������� ������������")
    @Description("���� ���� ��������� �������� ��������� ������ ������������")
    @Severity(SeverityLevel.CRITICAL)
    @Story("��������� ������ ������������")
    public void testSuccessGetUser() {
        int userId;
        Response response =
                given().header("Content-Type", "application/json").pathParam("userName", createdUser.getUsername())
                        .log().ifValidationFails()
                        .when().get("/{userName}")
                        .then()
                        .statusCode(200)
                        .extract().response();
        userId = response.jsonPath().getInt("id");
        Assertions.assertEquals(userId, 1, "User Id must be equal to 1");
    }



    @Test
    @DisplayName("������������ ��������� ��������������� ������������")
    @Description("���� ���� ��������� ������� ��������� ��������������� ������������")
    @Severity(SeverityLevel.NORMAL)
    @Story("��������� ��������������� ������������")
    public void testFailedGetUser() {
        Response response =
                given().header("Content-Type", "application/json").pathParam("userName", "notExist")
                        .log().ifValidationFails() // ���������� ������ ��� ������� �����
                        .when().get("/{userName}")
                        .then()
                        .statusCode(404)
                        .extract().response();
    }

    @Test
    @DisplayName("������������ ��������� �����")
    @Description("���� ���� ��������� �������� ���� ������������ � �������")
    @Severity(SeverityLevel.CRITICAL)
    @Story("���� � �������")
    public void testSuccessLogin() {
        given().header("Accept", "application/json").pathParam("username", createdUser.getUsername()).pathParam("password", createdUser.getPassword())
                .when().get("/login?username={username}&password={password}")
                .then().statusCode(200);
    }

    @Test
    @DisplayName("������������ ���������� �����")
    @Description("���� ���� ��������� ��������� ���� � �������� �������")
    @Severity(SeverityLevel.NORMAL)
    @Story("�������� ����")
    public void testFailedLogin() {
        given().header("Accept", "application/json").pathParam("username", createdUser.getUsername()).pathParam("password", 123123)
                .when().get("/login?username={username}&password={password}")
                .then().statusCode(200);
    }

    @Test
    @DisplayName("������������ ��������� ������ �� ��������")
    @Description("���� ���� ��������� �������� ����� ������������ �� ��������")
    @Severity(SeverityLevel.NORMAL)
    @Story("����� �� ��������")
    public void testSuccessLogout() {
        given().header("Accept", "application/json")
                .when().get("/logout")
                .then().statusCode(200);
    }

    @Test
    @DisplayName("������������ ��������� ���������� ������������")
    @Description("���� ���� ��������� �������� ���������� ������ ������������")
    @Severity(SeverityLevel.NORMAL)
    @Story("���������� ������ ������������")
    public void testUpdateUser() {
        UserSchema newUser = new UserSchema(1, "test1", "ilya", "butterfly", "jecki@mail.ru", "12345678", "89966666669", 0);
        given().header("Content-Type", "application/json").header("Accept", "application/json").pathParam("userName", createdUser.getUsername())
                .body(newUser)
                .log().ifValidationFails()
                .when().put("/{userName}")
                .then().statusCode(200);

        given().header("Content-Type", "application/json").header("Accept", "application/json").pathParam("userName", newUser.getUsername())
                .when().get("/{userName}")
                .then().statusCode(200)
                .body("id", equalTo(1))
                .body("username", equalTo("test1"))
                .body("firstName", equalTo("ilya"))
                .body("lastName", equalTo("butterfly"))
                .body("email", equalTo("jecki@mail.ru"))
                .body("password", equalTo("12345678"))
                .body("phone", equalTo("89966666669"))
                .body("userStatus", equalTo(0));
    }

    @Test
    @DisplayName("������������ �������� ������������")
    @Description("���� ���� ��������� �������� �������� ������������")
    @Severity(SeverityLevel.CRITICAL)
    @Story("�������� ������������")
    public void testSuccessDeleteUser() {
        given().header("Accept", "application/json").pathParam("userName", createdUser.getUsername())
                .when().delete("/{userName}")
                .then().statusCode(200);
    }

    @Test
    @DisplayName("������������ �������� ��������������� ������������")
    @Description("���� ���� ��������� ������� ������� ��������������� ������������")
    @Severity(SeverityLevel.NORMAL)
    @Story("�������� ��������������� ������������")
    public void testFailedDeleteUser() {
        given().header("Accept", "application/json").pathParam("userName", "not_Exits")
                .when().delete("/{userName}")
                .then().statusCode(404);
    }

    @Test
    @DisplayName("������������ �������� ������ �������������")
    @Description("���� ���� ��������� �������� ������ �������������")
    @Severity(SeverityLevel.NORMAL)
    @Story("�������� ������ �������������")
    public void testSuccessCreateUsersWithArray() {
        List<UserSchema> users = new ArrayList<>();
        users.add(new UserSchema(2, "test2", "�����", "��������", "fir@mail.ru", "123441243", "89546563945", 1));
        users.add(new UserSchema(3, "test3", "������", "��������", "test@yandex.com", "sd2ASe3sdffgb", "89678452134", 2));
        users.add(new UserSchema(4, "test4", "������", "�������", "jios@gmail.com", "*9df990erSf00-d", "89455382437", 0));
        given().header("Content-Type", "application/json").header("Accept", "application/json")
                .body(users)
                .when().post("/createWithArray")
                .then().statusCode(200);
    }

    @Test
    @DisplayName("������������ �������� ������ �� ������ ������������� �������������")
    @Description("���� ���� ��������� �������� ������ �������������")
    @Severity(SeverityLevel.NORMAL)
    @Story("�������� ������ �� ������ �������������")
    public void testSCreateUsersWithNullArray() {
        List<UserSchema> users = new ArrayList<>();
        given().header("Content-Type", "application/json").header("Accept", "application/json")
                .body(users)
                .when().post("/createWithArray")
                .then().statusCode(200);
    }

    @Test
    @DisplayName("������������ �������� ������ �������������")
    @Description("���� ���� ��������� �������� ������ ������������� � �������������� List")
    @Severity(SeverityLevel.NORMAL)
    @Story("�������� ������ �������������")
    public void testSuccessCreateUsersWithList() {
        List<UserSchema> users = new ArrayList<>();
        users.add(new UserSchema(2, "test2", "�����", "��������", "fir@mail.ru", "123441243", "89546563945", 1));
        users.add(new UserSchema(3, "test3", "������", "��������", "test@yandex.com", "sd2ASe3sdffgb", "89678452134", 2));
        users.add(new UserSchema(4, "test4", "������", "�������", "jios@gmail.com", "*9df990erSf00-d", "89455382437", 0));
        given().header("Content-Type", "application/json").header("Accept", "application/json")
                .body(users)
                .when().post("/createWithList")
                .then().statusCode(200);
    }

    @Test
    @DisplayName("������������ �������� ������ ������ �������������")
    @Description("���� ���� ��������� �������� ������ ������������� � �������������� List")
    @Severity(SeverityLevel.NORMAL)
    @Story("�������� ������ �������������")
    public void testCreateUsersNullWithList() {
        List<UserSchema> users = new ArrayList<>();
        given().header("Content-Type", "application/json").header("Accept", "application/json")
                .body(users)
                .when().post("/createWithList")
                .then().statusCode(200);
    }
}
