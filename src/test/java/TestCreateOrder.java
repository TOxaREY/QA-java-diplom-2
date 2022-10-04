import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;

@DisplayName("Тесты создания заказа")
public class TestCreateOrder {
    private String accessToken;

    private Response responseCreateUser(File json) {
        return given().header("Content-type", "application/json")
                .and()
                .body(json)
                .when()
                .post("/api/auth/register");
    }

    private Response responseCreateOrderWithAuthorization(File json) {
        return given().header("Content-type", "application/json")
                .and()
                .header("Authorization", accessToken)
                .and()
                .body(json)
                .when()
                .post("/api/orders");
    }

    private Response responseCreateOrderWithoutAuthorization(File json) {
        return given().header("Content-type", "application/json")
                .and()
                .body(json)
                .when()
                .post("/api/orders");
    }

    private void setAccessToken(Response response) {
        JsonPath jsonPathEvaluator = response.jsonPath();
        accessToken = jsonPathEvaluator.get("accessToken").toString();
    }

    private void testNegative400(Response response) {
        response.then().assertThat()
                .statusCode(400)
                .and()
                .body("success", is(false))
                .and()
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    private void testNegative401(Response response) {
        response.then().assertThat()
                .statusCode(401)
                .and()
                .body("success", is(false))
                .and()
                .body("message", equalTo("You should be authorised"));
    }

    private void testNegative500(Response response) {
        response.then().assertThat()
                .statusCode(500);
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = Constants.BASEURI;
    }

    @Test
    @DisplayName("Тест создания заказа с ингредиентами с авторизацией")
    public void testCreateOrderWithIngredientsWithAuthorization() {
        File json = new File("src/test/resources/userFull.json");
        Response response = responseCreateUser(json);
        setAccessToken(response);
        File jsonIngredients = new File("src/test/resources/ingredients.json");
        responseCreateOrderWithAuthorization(jsonIngredients).then().assertThat()
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
        Response response = responseCreateUser(json);
        setAccessToken(response);
        File jsonIngredients = new File("src/test/resources/ingredients.json");
        testNegative401(responseCreateOrderWithoutAuthorization(jsonIngredients));
    }

    @Test
    @DisplayName("Тест создания заказа без ингредиентов с авторизацией")
    public void testCreateOrderWithoutIngredientsWithAuthorization() {
        File json = new File("src/test/resources/userFull.json");
        Response response = responseCreateUser(json);
        setAccessToken(response);
        File jsonIngredientsEmpty = new File("src/test/resources/ingredientsEmpty.json");
        testNegative400(responseCreateOrderWithAuthorization(jsonIngredientsEmpty));
    }

    @Test
    @DisplayName("Тест создания заказа без ингредиентов без авторизации")
    public void testCreateOrderWithoutIngredientsWithoutAuthorization() {
        File json = new File("src/test/resources/userFull.json");
        Response response = responseCreateUser(json);
        setAccessToken(response);
        File jsonIngredientsEmpty = new File("src/test/resources/ingredientsEmpty.json");
        testNegative401(responseCreateOrderWithoutAuthorization(jsonIngredientsEmpty));
    }

    @Test
    @DisplayName("Тест создания заказа с неверным хешем ингредиентов с авторизацией")
    public void testCreateOrderWithWrongHashIngredientsWithAuthorization() {
        File json = new File("src/test/resources/userFull.json");
        Response response = responseCreateUser(json);
        setAccessToken(response);
        File jsonIngredientsWrongHash = new File("src/test/resources/ingredientsWrongHash.json");
        testNegative500(responseCreateOrderWithAuthorization(jsonIngredientsWrongHash));
    }

    @Test
    @DisplayName("Тест создания заказа с неверным хешем ингредиентов без авторизации")
    public void testCreateOrderWithWrongHashIngredientsWithoutAuthorization() {
        File json = new File("src/test/resources/userFull.json");
        Response response = responseCreateUser(json);
        setAccessToken(response);
        File jsonIngredientsWrongHash = new File("src/test/resources/ingredientsWrongHash.json");
        testNegative401(responseCreateOrderWithoutAuthorization(jsonIngredientsWrongHash));
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
