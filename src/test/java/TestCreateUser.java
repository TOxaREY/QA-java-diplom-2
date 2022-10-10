import com.google.gson.Gson;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import src.main.java.api.client.UserClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;

@DisplayName("Тесты создания пользователя")
public class TestCreateUser extends BaseTest {
    private String accessToken;
    Gson g = new Gson();
    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("Тест создания уникального пользователя")
    public void testUniqueUserCreation() throws FileNotFoundException {
        File json = new File("src/test/resources/userFull.json");
        BufferedReader br = new BufferedReader(new FileReader("src/test/resources/userFull.json"));
        User user = g.fromJson(br, User.class);
        UserClient userClient = new UserClient();
        Response response = userClient.responseCreateUser(json);
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
        accessToken = userClient.setAccessToken(response);
    }

    @Test
    @DisplayName("Тест создания пользователя, который уже зарегистрирован")
    public void testCreateUserWhoIsAlreadyRegistered() {
        File json = new File("src/test/resources/userFull.json");
        UserClient userClient = new UserClient();
        accessToken = userClient.setAccessToken(userClient.responseCreateUser(json));
        userClient.testNegative403(userClient.responseCreateUser(json), "User already exists");
    }

    @Test
    @DisplayName("Тест создания пользователя без поля email")
    public void testCreateUserWithoutEmailField() {
        File json = new File("src/test/resources/userWithoutEmailField.json");
        UserClient userClient = new UserClient();
        userClient.testNegative403(userClient.responseCreateUser(json), "Email, password and name are required fields");
    }

    @Test
    @DisplayName("Тест создания пользователя без поля password")
    public void testCreateUserWithoutPasswordField() {
        File json = new File("src/test/resources/userWithoutPasswordField.json");
        UserClient userClient = new UserClient();
        userClient.testNegative403(userClient.responseCreateUser(json), "Email, password and name are required fields");
    }

    @Test
    @DisplayName("Тест создания пользователя без поля name")
    public void testCreateUserWithoutNameField() {
        File json = new File("src/test/resources/userWithoutNameField.json");
        UserClient userClient = new UserClient();
        userClient.testNegative403(userClient.responseCreateUser(json), "Email, password and name are required fields");
    }

    @After
    public void tearDown() {
        super.tearDown(accessToken);
    }
}
