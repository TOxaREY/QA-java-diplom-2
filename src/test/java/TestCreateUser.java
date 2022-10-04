import com.google.gson.Gson;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;

@DisplayName("Тесты создания пользователя")
public class TestCreateUser {
    private String accessToken;
    private Response response(File json) {
        return given().header("Content-type", "application/json")
                .and()
                .body(json)
                .when()
                .post("/api/auth/register");
    }
    private void setAccessToken(Response response) {
        JsonPath jsonPathEvaluator = response.jsonPath();
        accessToken = jsonPathEvaluator.get("accessToken").toString();
    }
    private void testNegative(Response response, String message) {
        response.then().assertThat()
                .statusCode(403)
                .and()
                .body("success", is(false))
                .and()
                .body("message", equalTo(message));
    }
    Gson g = new Gson();
    @Before
    public void setUp() {
        RestAssured.baseURI = Constants.BASEURI;
    }

    @Test
    @DisplayName("Тест создания уникального пользователя")
    public void testUniqueUserCreation() throws FileNotFoundException {
        File json = new File("src/test/resources/userFull.json");
        BufferedReader br = new BufferedReader(new FileReader("src/test/resources/userFull.json"));
        User user = g.fromJson(br, User.class);
        Response response = response(json);
        response.then().assertThat()
                .statusCode(200)
                .and()
                .body("success", is(true))
                .and()
                .body("user.email", equalTo(user.email))
                .and()
                .body("user.name", equalTo(user.name))
                .and()
                .body("accessToken", notNullValue())
                .and()
                .body("refreshToken", notNullValue());
        setAccessToken(response);
    }

    @Test
    @DisplayName("Тест создания пользователя, который уже зарегистрирован")
    public void testCreateUserWhoIsAlreadyRegistered() {
        File json = new File("src/test/resources/userFull.json");
        Response response = response(json);
        setAccessToken(response);
        testNegative(response(json), "User already exists");
    }

    @Test
    @DisplayName("Тест создания пользователя без поля email")
    public void testCreateUserWithoutEmailField() {
        File json = new File("src/test/resources/userWithoutEmailField.json");
        testNegative(response(json), "Email, password and name are required fields");
    }

    @Test
    @DisplayName("Тест создания пользователя без поля password")
    public void testCreateUserWithoutPasswordField() {
        File json = new File("src/test/resources/userWithoutPasswordField.json");
        testNegative(response(json), "Email, password and name are required fields");
    }

    @Test
    @DisplayName("Тест создания пользователя без поля name")
    public void testCreateUserWithoutNameField() {
        File json = new File("src/test/resources/userWithoutNameField.json");
        testNegative(response(json), "Email, password and name are required fields");
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            Response responseDelete =
                    given().header("Authorization", accessToken)
                            .when()
                            .delete("/api/auth/user");
            int statusId = responseDelete.statusCode();
            if (statusId != 202) {
                System.out.println("Не удалось удалить пользователя");
            }
        }
    }
}
