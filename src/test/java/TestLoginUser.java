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

@DisplayName("Тесты авторизации пользователя")
public class TestLoginUser {
    private String accessToken;
    private Response response(File json, String path) {
        return given().header("Content-type", "application/json")
                .and()
                .body(json)
                .when()
                .post("/api/auth/" + path);
    }
    private void setAccessToken(Response response) {
        JsonPath jsonPathEvaluator = response.jsonPath();
        accessToken = jsonPathEvaluator.get("accessToken").toString();
    }

    private void testNegative(Response response) {
        response.then().assertThat()
                .statusCode(401)
                .and()
                .body("success", is(false))
                .and()
                .body("message", equalTo("email or password are incorrect"));
    }

    Gson g = new Gson();

    @Before
    public void setUp() {
        RestAssured.baseURI = Constants.BASEURI;
    }

    @Test
    @DisplayName("Тест авторизации под существующим пользователем")
    public void testLoginExistingUser() throws FileNotFoundException {
        File json = new File("src/test/resources/userFull.json");
        BufferedReader br = new BufferedReader(new FileReader("src/test/resources/userFull.json"));
        User user = g.fromJson(br, User.class);
        Response response = response(json, "register");
        setAccessToken(response);
        response(json, "login").then().assertThat()
                .statusCode(200)
                .and()
                .body("success", is(true))
                .and()
                .body("accessToken", notNullValue())
                .and()
                .body("refreshToken", notNullValue())
                .and()
                .body("user.email", equalTo(user.email))
                .and()
                .body("user.name", equalTo(user.name));
    }

    @Test
    @DisplayName("Тест авторизации с неверным логином")
    public void testLoginWrongLogin() {
        File json = new File("src/test/resources/userFull.json");
        File jsonWrongLogin = new File("src/test/resources/userWrongName.json");
        Response response = response(json, "register");
        setAccessToken(response);
        testNegative(response(jsonWrongLogin, "login"));
    }

    @Test
    @DisplayName("Тест авторизации с неверным паролем")
    public void testLoginWrongPassword()  {
        File json = new File("src/test/resources/userFull.json");
        File jsonWrongPassword = new File("src/test/resources/userWrongPassword.json");
        Response response = response(json, "register");
        setAccessToken(response);
        testNegative(response(jsonWrongPassword, "login"));
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
