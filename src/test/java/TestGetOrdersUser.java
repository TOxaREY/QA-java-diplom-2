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

@DisplayName("Тесты получения заказов конкретного пользователя")
public class TestGetOrdersUser {

    private String accessToken;

    private Response responseCreateUser(File json) {
        return given().header("Content-type", "application/json")
                .and()
                .body(json)
                .when()
                .post("/api/auth/register");
    }

    private void responseCreateOrderWithAuthorization(File json) {
        given().header("Content-type", "application/json")
                .and()
                .header("Authorization", accessToken)
                .and()
                .body(json)
                .when()
                .post("/api/orders");
    }

    private Response responseGetOrdersWithAuthorization() {
        return given().header("Authorization", accessToken)
                .when()
                .get("/api/orders");
    }

    private Response responseGetOrdersWithoutAuthorization() {
        return given().when()
                .get("/api/orders");
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

    @Before
    public void setUp() {
        RestAssured.baseURI = Constants.BASEURI;
    }

    @Test
    @DisplayName("Тест получения заказов конкретного пользователя с авторизацией")
    public void testGetOrdersUserWithAuthorization() {
        File json = new File("src/test/resources/userFull.json");
        Response response = responseCreateUser(json);
        setAccessToken(response);
        File jsonIngredients = new File("src/test/resources/ingredients.json");
        responseCreateOrderWithAuthorization(jsonIngredients);
        responseGetOrdersWithAuthorization().then().assertThat()
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
        Response response = responseCreateUser(json);
        setAccessToken(response);
        File jsonIngredients = new File("src/test/resources/ingredients.json");
        responseCreateOrderWithAuthorization(jsonIngredients);
        testNegative(responseGetOrdersWithoutAuthorization());
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
