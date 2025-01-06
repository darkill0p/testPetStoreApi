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


@Epic("Тестирование API endpoints для пользователей (Users)")
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
    @DisplayName("Тестирование успешной регистрации пользователя")
    @Description("Этот тест проверяет успешную регистрацию нового пользователя в системе")
    @Severity(SeverityLevel.NORMAL)
    @Story("Регистрация нового пользователя")
    public void testSuccessCreateUser() {
        UserSchema user = new UserSchema(1, "dd1", "Сергеевич", "Антонович", "ivanov@mail.ru", "12345678", "899999999999", 1);
        given().header("Content-Type", "application/json").header("Accept", "application/json")
                .body(user)
                .when().post()
                .then().statusCode(200);
    }

    @Test
    @DisplayName("Тестирование  регистрации пользователя без указания header application/json")
    @Description("Этот тест проверяет  регистрацию нового пользователя в системе")
    @Severity(SeverityLevel.NORMAL)
    @Story("Регистрация нового пользователя без указания header application/json")
    public void testSuccess1CreateUser() {
        UserSchema user = new UserSchema(1, "dd1", "Сергеевич", "Антонович", "ivanov@mail.ru", "12345678", "899999999999", 1);
        given().body(user)
                .when().post()
                .then().statusCode(415);
    }

    @Test
    @DisplayName("Тестирование  регистрации пользователя без указания данных")
    @Description("Этот тест проверяет  регистрацию нового пользователя в системе без указания данных")
    @Severity(SeverityLevel.NORMAL)
    @Story("Регистрация нового пользователя")
    public void testCreateUserWithNullData() {
        UserSchema user = new UserSchema();
        given().header("Content-Type", "application/json").header("Accept", "application/json")
                .body(user)
                .when().post()
                .then().statusCode(200);
    }


    @Test
    @DisplayName("Тестирование успешного получения пользователя")
    @Description("Этот тест проверяет успешное получение данных пользователя")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Получение данных пользователя")
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
    @DisplayName("Тестирование получения несуществующего пользователя")
    @Description("Этот тест проверяет попытку получения несуществующего пользователя")
    @Severity(SeverityLevel.NORMAL)
    @Story("Получение несуществующего пользователя")
    public void testFailedGetUser() {
        Response response =
                given().header("Content-Type", "application/json").pathParam("userName", "notExist")
                        .log().ifValidationFails() // Логировать только при провале теста
                        .when().get("/{userName}")
                        .then()
                        .statusCode(404)
                        .extract().response();
    }

    @Test
    @DisplayName("Тестирование успешного входа")
    @Description("Этот тест проверяет успешный вход пользователя в систему")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Вход в систему")
    public void testSuccessLogin() {
        given().header("Accept", "application/json").pathParam("username", createdUser.getUsername()).pathParam("password", createdUser.getPassword())
                .when().get("/login?username={username}&password={password}")
                .then().statusCode(200);
    }

    @Test
    @DisplayName("Тестирование неудачного входа")
    @Description("Этот тест проверяет неудачный вход с неверным паролем")
    @Severity(SeverityLevel.NORMAL)
    @Story("Неверный вход")
    public void testFailedLogin() {
        given().header("Accept", "application/json").pathParam("username", createdUser.getUsername()).pathParam("password", 123123)
                .when().get("/login?username={username}&password={password}")
                .then().statusCode(200);
    }

    @Test
    @DisplayName("Тестирование успешного выхода из аккаунта")
    @Description("Этот тест проверяет успешный выход пользователя из аккаунта")
    @Severity(SeverityLevel.NORMAL)
    @Story("Выход из аккаунта")
    public void testSuccessLogout() {
        given().header("Accept", "application/json")
                .when().get("/logout")
                .then().statusCode(200);
    }

    @Test
    @DisplayName("Тестирование успешного обновления пользователя")
    @Description("Этот тест проверяет успешное обновление данных пользователя")
    @Severity(SeverityLevel.NORMAL)
    @Story("Обновление данных пользователя")
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
    @DisplayName("Тестирование удаления пользователя")
    @Description("Этот тест проверяет успешное удаление пользователя")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Удаление пользователя")
    public void testSuccessDeleteUser() {
        given().header("Accept", "application/json").pathParam("userName", createdUser.getUsername())
                .when().delete("/{userName}")
                .then().statusCode(200);
    }

    @Test
    @DisplayName("Тестирование удаления несуществующего пользователя")
    @Description("Этот тест проверяет попытку удалить несуществующего пользователя")
    @Severity(SeverityLevel.NORMAL)
    @Story("Удаление несуществующего пользователя")
    public void testFailedDeleteUser() {
        given().header("Accept", "application/json").pathParam("userName", "not_Exits")
                .when().delete("/{userName}")
                .then().statusCode(404);
    }

    @Test
    @DisplayName("Тестирование создания списка пользователей")
    @Description("Этот тест проверяет создание списка пользователей")
    @Severity(SeverityLevel.NORMAL)
    @Story("Создание списка пользователей")
    public void testSuccessCreateUsersWithArray() {
        List<UserSchema> users = new ArrayList<>();
        users.add(new UserSchema(2, "test2", "Дарья", "Тестовая", "fir@mail.ru", "123441243", "89546563945", 1));
        users.add(new UserSchema(3, "test3", "Никита", "Михайлов", "test@yandex.com", "sd2ASe3sdffgb", "89678452134", 2));
        users.add(new UserSchema(4, "test4", "Максим", "Дмитров", "jios@gmail.com", "*9df990erSf00-d", "89455382437", 0));
        given().header("Content-Type", "application/json").header("Accept", "application/json")
                .body(users)
                .when().post("/createWithArray")
                .then().statusCode(200);
    }

    @Test
    @DisplayName("Тестирование создания списка из пустых пользователей пользователей")
    @Description("Этот тест проверяет создание списка пользователей")
    @Severity(SeverityLevel.NORMAL)
    @Story("Создание списка из пустых пользователей")
    public void testSCreateUsersWithNullArray() {
        List<UserSchema> users = new ArrayList<>();
        given().header("Content-Type", "application/json").header("Accept", "application/json")
                .body(users)
                .when().post("/createWithArray")
                .then().statusCode(200);
    }

    @Test
    @DisplayName("Тестирование создания списка пользователей")
    @Description("Этот тест проверяет создание списка пользователей с использованием List")
    @Severity(SeverityLevel.NORMAL)
    @Story("Создание списка пользователей")
    public void testSuccessCreateUsersWithList() {
        List<UserSchema> users = new ArrayList<>();
        users.add(new UserSchema(2, "test2", "Дарья", "Тестовая", "fir@mail.ru", "123441243", "89546563945", 1));
        users.add(new UserSchema(3, "test3", "Никита", "Михайлов", "test@yandex.com", "sd2ASe3sdffgb", "89678452134", 2));
        users.add(new UserSchema(4, "test4", "Максим", "Дмитров", "jios@gmail.com", "*9df990erSf00-d", "89455382437", 0));
        given().header("Content-Type", "application/json").header("Accept", "application/json")
                .body(users)
                .when().post("/createWithList")
                .then().statusCode(200);
    }

    @Test
    @DisplayName("Тестирование создания списка пустых пользователей")
    @Description("Этот тест проверяет создание списка пользователей с использованием List")
    @Severity(SeverityLevel.NORMAL)
    @Story("Создание списка пользователей")
    public void testCreateUsersNullWithList() {
        List<UserSchema> users = new ArrayList<>();
        given().header("Content-Type", "application/json").header("Accept", "application/json")
                .body(users)
                .when().post("/createWithList")
                .then().statusCode(200);
    }
}
