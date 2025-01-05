package pets;

import apiModels.Category;
import apiModels.Pet;
import apiModels.Tag;
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

public class TestPetService {

    static Pet memPet = null;

    @BeforeAll
    static void setUp() throws Exception {
        RestAssured.baseURI = "https://petstore.swagger.io/v2/pet";
    }

    @BeforeEach
    void setUpPet() {
            Category category = new Category(2L, "���");
            Tag tag = new Tag(1L, "������");
            Tag tag2 = new Tag(2L, "�����������");
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
    @DisplayName("������������ ��������� ��������� �������")
    public void testSuccessCreatePet() {
        Category category = new Category(1L, "���");
        Tag tag = new Tag(1L, "����� ���");
        Tag tag2 = new Tag(2L, "�����");
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

    @DisplayName("������������ ��������� �������� �� �������")
    @ParameterizedTest
    @ValueSource(strings = {"available", "pending", "sold"})
    public void testSuccessGetPetsByStatus(String status){
        Response response = given().header("Content-Type", "application/json").header("Accept", "application/json")
                .when().get("/findByStatus?status="+status)
                .then().statusCode(200).extract().response();
        List<Pet> pets = response.jsonPath().getList(".", Pet.class);
        for(Pet pet : pets){
            Assertions.assertEquals(pet.getStatus(), status);
        }
    }

    @DisplayName("������������ ��������� �������� �� ��������������� �������")
    @Test
    public void testFailedGetPetsByStatus(){
        Response response = given().header("Content-Type", "application/json").header("Accept", "application/json")
                .when().get("/findByStatus?status="+"notExist")
                .then().statusCode(200).extract().response();
        List<Pet> pets = response.jsonPath().getList(".", Pet.class);
        for(Pet pet : pets){
            Assertions.assertEquals(pet.getStatus(), "notExist");
        }
    }

    @DisplayName("������������ ��������� ������� �� id")
    @Test
    public void testSuccessGetPetById(){
        Response response = given().header("Content-Type", "application/json").header("Accept", "application/json")
                .when().get("/"+memPet.getId())
                .then().statusCode(200).extract().response();
        Pet getPet = response.getBody().as(Pet.class);
        Assertions.assertEquals(memPet, getPet);
    }

    @DisplayName("������������ ��������� ������� �� ��������������� id")
    @Test
    public void testFailedGetPetById(){
        Response response = given().header("Content-Type", "application/json").header("Accept", "application/json")
                .when().get("/"+0)
                .then().statusCode(404).extract().response();
    }

    @DisplayName("������������ ���������� ������� �� id")
    @Test
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

    @DisplayName("������������ ���������� ������� �� ��������������� id")
    @Test
    public void testFailedUpdatePetById(){
        String newNameAndStatus = "name=TestName&status=sold";
        given().header("Content-Type", "application/x-www-form-urlencoded").header("Accept", "application/json")
                .body(newNameAndStatus)
                .when().post("/"+0)
                .then().statusCode(404).extract().response();



    }

    @DisplayName("������������ �������� ������� �� id")
    @Test
    public void testSuccessDeletePet(){
        String newNameAndStatus = "name=TestName&status=sold";
        given().header("api_key", "special-key").header("Accept", "application/json")
                .body(newNameAndStatus)
                .when().delete("/"+memPet.getId())
                .then().statusCode(200);

    }

    @DisplayName("������������ �������� ������� �� ��������������� id")
    @Test
    public void testFailedDeletePet(){
        String newNameAndStatus = "name=TestName&status=sold";
        given().header("api_key", "special-key").header("Accept", "application/json")
                .body(newNameAndStatus)
                .when().delete("/"+0)
                .then().statusCode(404);

    }

    @DisplayName("������������ �������� ������� �� id ��� �������� api key")
    @Test
    public void testDeletePetWithoutProvidingApiKey(){
        String newNameAndStatus = "name=TestName&status=sold";
        given().header("Accept", "application/json")
                .body(newNameAndStatus)
                .when().delete("/"+memPet.getId())
                .then().statusCode(200);

    }

    @DisplayName("������������ ���������� �������")
    @Test
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

    @DisplayName("������������ ������������ ��������")
    @Test
    public void testSuccessUploadImage(){
        File file = new File("src/test/resources/images.jpg");
        given().header("Content-Type", "multipart/form-data").header("Accept", "application/json")
                .multiPart("additionalMetadata", "additional")
                .multiPart("file", file, "image/jpeg")
                .when().post("/"+memPet.getId()+"/uploadImage")
                .then().statusCode(200);

    }

    @DisplayName("������������ ����� �������� �� �����")
    @Test
    @Deprecated
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
