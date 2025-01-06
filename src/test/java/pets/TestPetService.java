package pets;

import apiModels.Category;
import apiModels.Pet;
import apiModels.Tag;
import io.qameta.allure.Epic;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@Epic("Тестирование API endpoints для питомцев (Pets)")
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
    @Step("Проверка успешного создания питомца")
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


    @Test
    @DisplayName("Тестирование  создания питомца без указания данных")
    @Step("Проверка успешного создания питомца")
    public void testCreatePetWithNoData() {

        Pet pet = new Pet();
        Response response = given().header("Content-Type", "application/json").header("Accept", "application/json")
                .body(pet)
                .when().post()
                .then().statusCode(200).extract().response();

    }

    @Test
    @DisplayName("Тестирование  создания питомца без указания данных")
    @Step("Проверка успешного создания питомца")
    public void testCreatePetWithNoHeaders() {

        Pet pet = new Pet();
        Response response = given()
                .body(pet)
                .when().post()
                .then().statusCode(415).extract().response();

    }

    @Test
    @DisplayName("Тестирование создания питомца без указания массива фотографий")
    @Step("Проверка создания питомца без указания массива фотографий")
    public void testCreatePetWithNoPhostos() {
        Category category = new Category(1L, "Пес");
        Tag tag = new Tag(1L, "Много ест");
        Tag tag2 = new Tag(2L, "Белый");
        List<Tag> tagList = new ArrayList<>();
        tagList.add(tag);
        tagList.add(tag2);
        List<String> photos = new ArrayList<>();


        Pet pet = new Pet(2L, category, "Whiteman", photos, tagList, "available");
        pet.setPhotoUrls(null);
        Response response = given().header("Content-Type", "application/json").header("Accept", "application/json")
                .body(pet)
                .when().post()
                .then().statusCode(200).extract().response();

    }

    @Test
    @DisplayName("Тестирование создания питомца без указания тэгов")
    @Step("Проверка создания питомца без указания тэгов")
    public void testCreatePetWithNoTags() {
        Category category = new Category(1L, "Пес");
        List<Tag> tagList = new ArrayList<>();
        List<String> photos = new ArrayList<>();


        Pet pet = new Pet(2L, category, "Whiteman", photos, tagList, "available");
        pet.setTags(null);
        Response response = given().header("Content-Type", "application/json").header("Accept", "application/json")
                .body(pet)
                .when().post()
                .then().statusCode(200).extract().response();

    }

    @DisplayName("Тестирование получение питомцев по статусу")
    @ParameterizedTest
    @ValueSource(strings = {"available", "pending", "sold"})
    @Story("Получение питомцев")
    @Step("Проверка питомцев со статусом {status}")
    public void testSuccessGetPetsByStatus(String status){
        Response response = given().header("Content-Type", "application/json").header("Accept", "application/json")
                .when().get("/findByStatus?status="+status)
                .then().statusCode(200).extract().response();
        List<Pet> pets = response.jsonPath().getList(".", Pet.class);
        for(Pet pet : pets){
            Assertions.assertEquals(pet.getStatus(), status);
        }
    }

    @DisplayName("Тестирование получение питомцев по статусам")
    @Test
    @Story("Получение питомцев")
    @Step("Проверка питомцев со статусом {status}")
    public void testSuccessGetPetsByStatuses(){
        String status = "status=available&status=pending&status=sold";
        Response response = given().header("Content-Type", "application/json").header("Accept", "application/json")
                .when().get("/findByStatus?"+status)
                .then().statusCode(200).extract().response();

    }

    @DisplayName("Тестирование получение питомцев по несуществующему статусу")
    @Test
    @Step("Получение питомца по несуществующему статусу")
    public void testFailedGetPetsByStatus(){
        Response response = given().header("Content-Type", "application/json").header("Accept", "application/json")
                .when().get("/findByStatus?status="+"notExist")
                .then().statusCode(200).extract().response();
        List<Pet> pets = response.jsonPath().getList(".", Pet.class);
        for(Pet pet : pets){
            Assertions.assertEquals(pet.getStatus(), "notExist");
        }
    }

    @DisplayName("Тестирование получение питомца по id")
    @Test
    @Step("Проверка получения питомца по ID")
    public void testSuccessGetPetById(){
        Response response = given().header("Content-Type", "application/json").header("Accept", "application/json")
                .when().get("/"+memPet.getId())
                .then().statusCode(200).extract().response();
        Pet getPet = response.getBody().as(Pet.class);
        Assertions.assertEquals(memPet, getPet);
    }

    @DisplayName("Тестирование получение питомца по несуществующему id")
    @Test
    @Step("Проверка получения питомца по несуществующему ID")
    public void testFailedGetPetById(){
        Response response = given().header("Content-Type", "application/json").header("Accept", "application/json")
                .when().get("/"+0)
                .then().statusCode(404).extract().response();
    }

    @DisplayName("Тестирование обновление питомца по id")
    @Test
    @Step("Тестирование обновление питомца по id")
    public void testSuccessUpdatePetById(){
        String newNameAndStatus = "name=TestName&status=sold";
        given().header("Content-Type", "application/x-www-form-urlencoded").header("Accept", "application/json")
                .body(newNameAndStatus)
                .when().post("/"+memPet.getId())
                .then().statusCode(200).extract().response();

        given().header("Content-Type", "application/json").header("Accept", "application/json")
                .when().get("/"+memPet.getId())
                .then().statusCode(200).body("name", equalTo("TestName"))
                .body("status", equalTo("sold"));

    }

    @DisplayName("Тестирование обновление питомца по несуществующему id")
    @Test
    @Step("обновление питомца по несуществующему id")
    public void testFailedUpdatePetById(){
        String newNameAndStatus = "name=TestName&status=sold";
        given().header("Content-Type", "application/x-www-form-urlencoded").header("Accept", "application/json")
                .body(newNameAndStatus)
                .when().post("/"+0)
                .then().statusCode(404).extract().response();



    }

    @DisplayName("Тестирование удаление питомца по id")
    @Test
    @Step("Удаление питомца по ID")
    public void testSuccessDeletePet(){
        String newNameAndStatus = "name=TestName&status=sold";
        given().header("api_key", "special-key").header("Accept", "application/json")
                .body(newNameAndStatus)
                .when().delete("/"+memPet.getId())
                .then().statusCode(200);

    }

    @DisplayName("Тестирование удаление питомца по несуществующему id")
    @Test
    @Step("Удаление питомца по несуществующему ID")
    public void testFailedDeletePet(){
        String newNameAndStatus = "name=TestName&status=sold";
        given().header("api_key", "special-key").header("Accept", "application/json")
                .body(newNameAndStatus)
                .when().delete("/"+0)
                .then().statusCode(404);

    }

    @DisplayName("Тестирование удаление питомца по id без указания api key")
    @Test
    @Step("Удаление питомца по  ID и без api key")
    public void testDeletePetWithoutProvidingApiKey(){
        String newNameAndStatus = "name=TestName&status=sold";
        given().header("Accept", "application/json")
                .body(newNameAndStatus)
                .when().delete("/"+memPet.getId())
                .then().statusCode(200);

    }

    @DisplayName("Тестирование обновление питомца")
    @Test
    @Step("обновление питомца put")
    public void testSuccessUpdatePet(){
        Pet tempPet = memPet.copy();
        tempPet.setName("TneNewNameByCopeThisObject");
        tempPet.setStatus("sold");

        given().header("Content-Type", "application/json").header("Accept", "application/json")
                .body(tempPet)
                .when().put("/")
                .then().statusCode(200);

        Response response = given().header("Content-Type", "application/json").header("Accept", "application/json")
                .when().get("/"+memPet.getId())
                .then().statusCode(200).extract().response();
        Pet getPet = response.getBody().as(Pet.class);
        Assertions.assertEquals(tempPet, getPet);
    }

    @DisplayName("Тестирование прикрепления картинки")
    @Test
    @Step("Загрузка изображения в профиль питомца")
    public void testSuccessUploadImage(){
        File file = new File("src/test/resources/images.jpg");
        given().header("Content-Type", "multipart/form-data").header("Accept", "application/json")
                .multiPart("additionalMetadata", "additional")
                .multiPart("file", file, "image/jpeg")
                .when().post("/"+memPet.getId()+"/uploadImage")
                .then().statusCode(200);

    }

    @DisplayName("Тестирование поиск питомцев по тэгам")
    @Test
    @Deprecated
    @Step("Тестирование поиск питомцев по тэгам")
    public void testDeprecatedMethodsFindPetsByTags(){
        StringBuilder uri = new StringBuilder();
        List<Tag> tags = memPet.getTags();
        for(Tag tag : tags){
            uri.append("tags=").append(tag.getName()).append("&");
        }
        Response response = given().header("Accept", "application/json").
                when().get("/findByTags?"+uri.toString())
                .then().statusCode(200).extract().response();
    }
}