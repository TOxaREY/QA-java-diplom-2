import com.google.gson.Gson;
import io.qameta.allure.junit4.DisplayName;
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

@DisplayName("Тесты авторизации пользователя")
public class TestLoginUser extends BaseTest {
    private String accessToken;
    Gson g = new Gson();

    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("Тест авторизации под существующим пользователем")
    public void testLoginExistingUser() throws FileNotFoundException {
        File json = new File("src/test/resources/userFull.json");
        BufferedReader br = new BufferedReader(new FileReader("src/test/resources/userFull.json"));
        User user = g.fromJson(br, User.class);
        UserClient userClient = new UserClient();
        accessToken = userClient.setAccessToken(userClient.responseAuthorizationUser(json, "register"));
        userClient.responseAuthorizationUser(json, "login")
                .then()
                .assertThat()
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
        UserClient userClient = new UserClient();
        accessToken = userClient.setAccessToken(userClient.responseAuthorizationUser(json, "register"));
        userClient.testNegative401(userClient.responseAuthorizationUser(jsonWrongLogin, "login"));
    }

    @Test
    @DisplayName("Тест авторизации с неверным паролем")
    public void testLoginWrongPassword()  {
        File json = new File("src/test/resources/userFull.json");
        File jsonWrongPassword = new File("src/test/resources/userWrongPassword.json");
        UserClient userClient = new UserClient();
        accessToken = userClient.setAccessToken(userClient.responseAuthorizationUser(json, "register"));
        userClient.testNegative401(userClient.responseAuthorizationUser(jsonWrongPassword, "login"));
    }

    @After
    public void tearDown() {
        super.tearDown(accessToken);
    }
}
