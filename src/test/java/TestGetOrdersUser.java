import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import src.main.java.api.client.OrdersClient;
import src.main.java.api.client.UserClient;

import java.io.File;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;

@DisplayName("Тесты получения заказов конкретного пользователя")
public class TestGetOrdersUser extends BaseTest {

    private String accessToken;

    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("Тест получения заказов конкретного пользователя с авторизацией")
    public void testGetOrdersUserWithAuthorization() {
        File json = new File("src/test/resources/userFull.json");
        UserClient userClient = new UserClient();
        accessToken = userClient.setAccessToken(userClient.responseCreateUser(json));
        File jsonIngredients = new File("src/test/resources/ingredients.json");
        OrdersClient ordersClient = new OrdersClient();
        ordersClient.responseCreateOrderWithAuthorization(jsonIngredients, accessToken);
        ordersClient.responseGetOrdersWithAuthorization(accessToken)
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", is(true))
                .and()
                .body("orders", notNullValue())
                .and()
                .body("total", notNullValue())
                .and()
                .body("totalToday", notNullValue());
    }

    @Test
    @DisplayName("Тест получения заказов конкретного пользователя без авторизацией")
    public void testGetOrdersUserWithoutAuthorization() {
        File json = new File("src/test/resources/userFull.json");
        UserClient userClient = new UserClient();
        accessToken = userClient.setAccessToken(userClient.responseCreateUser(json));
        File jsonIngredients = new File("src/test/resources/ingredients.json");
        OrdersClient ordersClient = new OrdersClient();
        ordersClient.responseCreateOrderWithAuthorization(jsonIngredients, accessToken);
        ordersClient.testNegative401(ordersClient.responseGetOrdersWithoutAuthorization());
    }

    @After
    public void tearDown() {
        super.tearDown(accessToken);
    }
}
