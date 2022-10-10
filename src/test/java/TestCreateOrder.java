import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import src.main.java.api.client.OrdersClient;
import src.main.java.api.client.UserClient;

import java.io.File;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;

@DisplayName("Тесты создания заказа")
public class TestCreateOrder extends BaseTest {
    private String accessToken;

    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("Тест создания заказа с ингредиентами с авторизацией")
    public void testCreateOrderWithIngredientsWithAuthorization() {
        File json = new File("src/test/resources/userFull.json");
        UserClient userClient = new UserClient();
        accessToken = userClient.setAccessToken(userClient.responseCreateUser(json));
        File jsonIngredients = new File("src/test/resources/ingredients.json");
        OrdersClient ordersClient = new OrdersClient();
        ordersClient.responseCreateOrderWithAuthorization(jsonIngredients, accessToken)
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", is(true))
                .and()
                .body("order.number", notNullValue())
                .and()
                .body("name", notNullValue());
    }

    @Test
    @DisplayName("Тест создания заказа с ингредиентами без авторизации")
    public void testCreateOrderWithIngredientsWithoutAuthorization() {
        File json = new File("src/test/resources/userFull.json");
        UserClient userClient = new UserClient();
        accessToken = userClient.setAccessToken(userClient.responseCreateUser(json));
        File jsonIngredients = new File("src/test/resources/ingredients.json");
        OrdersClient ordersClient = new OrdersClient();
        ordersClient.testNegative401(ordersClient.responseCreateOrderWithoutAuthorization(jsonIngredients));
    }

    @Test
    @DisplayName("Тест создания заказа без ингредиентов с авторизацией")
    public void testCreateOrderWithoutIngredientsWithAuthorization() {
        File json = new File("src/test/resources/userFull.json");
        UserClient userClient = new UserClient();
        accessToken = userClient.setAccessToken(userClient.responseCreateUser(json));
        File jsonIngredientsEmpty = new File("src/test/resources/ingredientsEmpty.json");
        OrdersClient ordersClient = new OrdersClient();
        ordersClient.testNegative400(ordersClient.responseCreateOrderWithAuthorization(jsonIngredientsEmpty, accessToken));
    }

    @Test
    @DisplayName("Тест создания заказа без ингредиентов без авторизации")
    public void testCreateOrderWithoutIngredientsWithoutAuthorization() {
        File json = new File("src/test/resources/userFull.json");
        UserClient userClient = new UserClient();
        accessToken = userClient.setAccessToken(userClient.responseCreateUser(json));
        File jsonIngredientsEmpty = new File("src/test/resources/ingredientsEmpty.json");
        OrdersClient ordersClient = new OrdersClient();
        ordersClient.testNegative401(ordersClient.responseCreateOrderWithoutAuthorization(jsonIngredientsEmpty));
    }

    @Test
    @DisplayName("Тест создания заказа с неверным хешем ингредиентов с авторизацией")
    public void testCreateOrderWithWrongHashIngredientsWithAuthorization() {
        File json = new File("src/test/resources/userFull.json");
        UserClient userClient = new UserClient();
        accessToken = userClient.setAccessToken(userClient.responseCreateUser(json));
        File jsonIngredientsWrongHash = new File("src/test/resources/ingredientsWrongHash.json");
        OrdersClient ordersClient = new OrdersClient();
        ordersClient.testNegative500(ordersClient.responseCreateOrderWithAuthorization(jsonIngredientsWrongHash, accessToken));
    }

    @Test
    @DisplayName("Тест создания заказа с неверным хешем ингредиентов без авторизации")
    public void testCreateOrderWithWrongHashIngredientsWithoutAuthorization() {
        File json = new File("src/test/resources/userFull.json");
        UserClient userClient = new UserClient();
        accessToken = userClient.setAccessToken(userClient.responseCreateUser(json));
        File jsonIngredientsWrongHash = new File("src/test/resources/ingredientsWrongHash.json");
        OrdersClient ordersClient = new OrdersClient();
        ordersClient.testNegative401(ordersClient.responseCreateOrderWithoutAuthorization(jsonIngredientsWrongHash));
    }

    @After
    public void tearDown() {
        super.tearDown(accessToken);
    }
}
