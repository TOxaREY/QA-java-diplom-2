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
import static org.hamcrest.core.Is.is;

@DisplayName("Тесты изменения данных пользователя")
public class TestUpdateDateUser {
    private String accessToken;

    private Response responseCreate(File json) {
        return given().header("Content-type", "application/json")
                .and()
                .body(json)
                .when()
                .post("/api/auth/register");
    }

    private Response responseUpdateWithAuthorization(File json) {
        return given().header("Content-type", "application/json")
                .and()
                .header("Authorization", accessToken)
                .and()
                .body(json)
                .when()
                .patch("/api/auth/user");
    }

    private Response responseUpdateWithoutAuthorization(File json) {
        return given().header("Content-type", "application/json")
                .and()
                .body(json)
                .when()
                .patch("/api/auth/user");
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
                .body("message", equalTo("You should be authorised"));
    }
    Gson g = new Gson();

    @Before
    public void setUp() {
        RestAssured.baseURI = Uri.BASEURI;
    }

    @Test
    @DisplayName("Тест изменения поля email пользователя с авторизацией")
    public void testUpdateEmailFieldUserWithAuthorization() throws FileNotFoundException {
        File json = new File("src/test/resources/userFull.json");
        Response response = responseCreate(json);
        setAccessToken(response);
        File jsonNewEmail = new File("src/test/resources/userNewEmail.json");
        BufferedReader br = new BufferedReader(new FileReader("src/test/resources/userNewEmail.json"));
        User user = g.fromJson(br, User.class);
        responseUpdateWithAuthorization(jsonNewEmail).then().assertThat()
                .statusCode(200)
                .and()
                .body("success", is(true))
                .and()
                .body("user.email", equalTo(user.email))
                .and()
                .body("user.name", equalTo(user.name));
    }

    @Test
    @DisplayName("Тест изменения поля password пользователя с авторизацией")
    public void testUpdatePasswordFieldUserWithAuthorization() throws FileNotFoundException {
        File json = new File("src/test/resources/userFull.json");
        Response response = responseCreate(json);
        setAccessToken(response);
        File jsonNewPassword = new File("src/test/resources/userNewPassword.json");
        BufferedReader br = new BufferedReader(new FileReader("src/test/resources/userNewPassword.json"));
        User user = g.fromJson(br, User.class);
        responseUpdateWithAuthorization(jsonNewPassword).then().assertThat()
                .statusCode(200)
                .and()
                .body("success", is(true))
                .and()
                .body("user.email", equalTo(user.email))
                .and()
                .body("user.name", equalTo(user.name));
    }

    @Test
    @DisplayName("Тест изменения поля name пользователя с авторизацией")
    public void testUpdateNameFieldUserWithAuthorization() throws FileNotFoundException {
        File json = new File("src/test/resources/userFull.json");
        Response response = responseCreate(json);
        setAccessToken(response);
        File jsonNewName = new File("src/test/resources/userNewName.json");
        BufferedReader br = new BufferedReader(new FileReader("src/test/resources/userNewName.json"));
        User user = g.fromJson(br, User.class);
        responseUpdateWithAuthorization(jsonNewName).then().assertThat()
                .statusCode(200)
                .and()
                .body("success", is(true))
                .and()
                .body("user.email", equalTo(user.email))
                .and()
                .body("user.name", equalTo(user.name));
    }

    @Test
    @DisplayName("Тест изменения поля email пользователя без авторизации")
    public void testUpdateEmailFieldUserWithoutAuthorization() {
        File json = new File("src/test/resources/userFull.json");
        Response response = responseCreate(json);
        setAccessToken(response);
        File jsonNewEmail = new File("src/test/resources/userNewEmail.json");
        testNegative(responseUpdateWithoutAuthorization(jsonNewEmail));
    }

    @Test
    @DisplayName("Тест изменения поля password пользователя без авторизации")
    public void testUpdatePasswordFieldUserWithoutAuthorization() {
        File json = new File("src/test/resources/userFull.json");
        Response response = responseCreate(json);
        setAccessToken(response);
        File jsonNewPassword = new File("src/test/resources/userNewPassword.json");
        testNegative(responseUpdateWithoutAuthorization(jsonNewPassword));
    }

    @Test
    @DisplayName("Тест изменения поля name пользователя без авторизации")
    public void testUpdateNameFieldUserWithoutAuthorization() {
        File json = new File("src/test/resources/userFull.json");
        Response response = responseCreate(json);
        setAccessToken(response);
        File jsonNewName = new File("src/test/resources/userNewName.json");
        testNegative(responseUpdateWithoutAuthorization(jsonNewName));
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
