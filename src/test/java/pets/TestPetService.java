package pets;

import apiModels.Category;
import apiModels.Pet;
import apiModels.Tag;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@Epic("Тестированрие Api endpoints для работы с питомцами")
@Feature("Управление питомцами")
public class TestPetService {

    static Pet memPet = null;

    @BeforeAll
    static void setUp() throws Exception {
        RestAssured.baseURI = "https://petstore.swagger.io/v2/pet";
    }

    @BeforeEach
    void setUpPet() {
        Category category = new Category(2L, "Кот");
        Tag tag = new Tag(1L, "Лениый");
        Tag tag2 = new Tag(2L, "Психованный");
        List<Tag> tagList = new ArrayList<>();
        tagList.add(tag);
        tagList.add(tag2);
        List<String> photos = Arrays.asList("http://localhost/photos/1", "http://localhost/photos/2");

        Pet pet = new Pet(3L, category, "Pushok", photos, tagList, "available");
        Response response = given().header("Content-Type", "application/json").header("Accept", "application/json")
                .body(pet)
                .when().post()
                .then().statusCode(200).extract().response();
        pet.setId(response.getBody().jsonPath().getLong("id"));
        memPet = pet;
    }

    @Test
    @DisplayName("Тестирование успешного создаания питомца")
    @Story("Создание нового питомца")
    @TmsLink("PET-101")
    public void testSuccessCreatePet() {
        Category category = new Category(1L, "Пес");
        Tag tag = new Tag(1L, "Много ест");
        Tag tag2 = new Tag(2L, "Белый");
        List<Tag> tagList = new ArrayList<>();
        tagList.add(tag);
        tagList.add(tag2);
        List<String> photos = Arrays.asList("http://localhost/photos/1", "http://localhost/photos/2");

        Pet pet = new Pet(2L, category, "Whiteman", photos, tagList, "available");
        Response response = given().header("Content-Type", "application/json").header("Accept", "application/json")
                .body(pet)
                .when().post()
                .then().statusCode(200).extract().response();
    }

    @DisplayName("Тестирование получение питомцев по статусу")
    @ParameterizedTest
    @ValueSource(strings = {"available", "pending", "sold"})
    @Story("Поиск питомцев по статусу")
    public void testSuccessGetPetsByStatus(String status) {
        Response response = given().header("Content-Type", "application/json").header("Accept", "application/json")
                .when().get("/findByStatus?status=" + status)
                .then().statusCode(200).extract().response();
        List<Pet> pets = response.jsonPath().getList(".", Pet.class);
        for (Pet pet : pets) {
            Assertions.assertEquals(pet.getStatus(), status);
        }
    }

    @DisplayName("Тестирование получение питомцев по несуществующему статусу")
    @Test
    @Story("Ошибка при поиске питомцев по несуществующему статусу")
    public void testFailedGetPetsByStatus() {
        Response response = given().header("Content-Type", "application/json").header("Accept", "application/json")
                .when().get("/findByStatus?status=" + "notExist")
                .then().statusCode(200).extract().response();
        List<Pet> pets = response.jsonPath().getList(".", Pet.class);
        for (Pet pet : pets) {
            Assertions.assertEquals(pet.getStatus(), "notExist");
        }
    }

    @DisplayName("Тестирование получение питомца по id")
    @Test
    @Story("Получение питомца по id")
    public void testSuccessGetPetById() {
        Response response = given().header("Content-Type", "application/json").header("Accept", "application/json")
                .when().get("/" + memPet.getId())
                .then().statusCode(200).extract().response();
        Pet getPet = response.getBody().as(Pet.class);
        Assertions.assertEquals(memPet, getPet);
    }

    @DisplayName("Тестирование обновление питомца по id")
    @Test
    @Story("Обновление питомца по id")
    public void testSuccessUpdatePetById() {
        String newNameAndStatus = "name=TestName&status=sold";
        given().header("Content-Type", "application/x-www-form-urlencoded").header("Accept", "application/json")
                .body(newNameAndStatus)
                .when().post("/" + memPet.getId())
                .then().statusCode(200).extract().response();

        given().header("Content-Type", "application/json").header("Accept", "application/json")
                .when().get("/" + memPet.getId())
                .then().statusCode(200).body("name", equalTo("TestName"))
                .body("status", equalTo("sold"));
    }

    @DisplayName("Тестирование прикрепления картинки")
    @Test
    @Story("Загрузка изображения для питомца")
    public void testSuccessUploadImage() {
        File file = new File("src/test/resources/images.jpg");
        given().header("Content-Type", "multipart/form-data").header("Accept", "application/json")
                .multiPart("additionalMetadata", "additional")
                .multiPart("file", file, "image/jpeg")
                .when().post("/" + memPet.getId() + "/uploadImage")
                .then().statusCode(200);
    }
}
