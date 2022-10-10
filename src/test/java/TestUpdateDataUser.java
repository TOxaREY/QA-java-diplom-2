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
import static org.hamcrest.core.Is.is;

@DisplayName("Тесты изменения данных пользователя")
public class TestUpdateDataUser extends BaseTest {
    private String accessToken;
    Gson g = new Gson();

    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("Тест изменения поля email пользователя с авторизацией")
    public void testUpdateEmailFieldUserWithAuthorization() throws FileNotFoundException {
        File json = new File("src/test/resources/userFull.json");
        UserClient userClient = new UserClient();
        accessToken = userClient.setAccessToken(userClient.responseCreateUser(json));
        File jsonNewEmail = new File("src/test/resources/userNewEmail.json");
        BufferedReader br = new BufferedReader(new FileReader("src/test/resources/userNewEmail.json"));
        User user = g.fromJson(br, User.class);
        userClient.responseUpdateWithAuthorization(jsonNewEmail, accessToken)
                .then()
                .assertThat()
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
        UserClient userClient = new UserClient();
        accessToken = userClient.setAccessToken(userClient.responseCreateUser(json));
        File jsonNewPassword = new File("src/test/resources/userNewPassword.json");
        BufferedReader br = new BufferedReader(new FileReader("src/test/resources/userNewPassword.json"));
        User user = g.fromJson(br, User.class);
        userClient.responseUpdateWithAuthorization(jsonNewPassword, accessToken)
                .then()
                .assertThat()
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
        UserClient userClient = new UserClient();
        accessToken = userClient.setAccessToken(userClient.responseCreateUser(json));
        File jsonNewName = new File("src/test/resources/userNewName.json");
        BufferedReader br = new BufferedReader(new FileReader("src/test/resources/userNewName.json"));
        User user = g.fromJson(br, User.class);
        userClient.responseUpdateWithAuthorization(jsonNewName, accessToken)
                .then()
                .assertThat()
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
        UserClient userClient = new UserClient();
        accessToken = userClient.setAccessToken(userClient.responseCreateUser(json));
        File jsonNewEmail = new File("src/test/resources/userNewEmail.json");
        userClient.testNegative401Author(userClient.responseUpdateWithoutAuthorization(jsonNewEmail));
    }

    @Test
    @DisplayName("Тест изменения поля password пользователя без авторизации")
    public void testUpdatePasswordFieldUserWithoutAuthorization() {
        File json = new File("src/test/resources/userFull.json");
        UserClient userClient = new UserClient();
        accessToken = userClient.setAccessToken(userClient.responseCreateUser(json));
        File jsonNewPassword = new File("src/test/resources/userNewPassword.json");
        userClient.testNegative401Author(userClient.responseUpdateWithoutAuthorization(jsonNewPassword));
    }

    @Test
    @DisplayName("Тест изменения поля name пользователя без авторизации")
    public void testUpdateNameFieldUserWithoutAuthorization() {
        File json = new File("src/test/resources/userFull.json");
        UserClient userClient = new UserClient();
        accessToken = userClient.setAccessToken(userClient.responseCreateUser(json));
        File jsonNewName = new File("src/test/resources/userNewName.json");
        userClient.testNegative401Author(userClient.responseUpdateWithoutAuthorization(jsonNewName));
    }

    @After
    public void tearDown() {
        super.tearDown(accessToken);
    }
}
