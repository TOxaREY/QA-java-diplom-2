package src.main.java.api.client;

import io.restassured.response.Response;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;

public class OrdersClient {
    public Response responseCreateOrderWithAuthorization(File json, String accessToken) {
        return given().header("Content-type", "application/json")
                .and()
                .header("Authorization", accessToken)
                .and()
                .body(json)
                .when()
                .post("/api/orders");
    }

    public Response responseCreateOrderWithoutAuthorization(File json) {
        return given().header("Content-type", "application/json")
                .and()
                .body(json)
                .when()
                .post("/api/orders");
    }

    public Response responseGetOrdersWithAuthorization(String accessToken) {
        return given().header("Authorization", accessToken)
                .when()
                .get("/api/orders");
    }

    public Response responseGetOrdersWithoutAuthorization() {
        return given().when()
                .get("/api/orders");
    }

    public void testNegative400(Response response) {
        response.then().assertThat()
                .statusCode(400)
                .and()
                .body("success", is(false))
                .and()
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    public void testNegative401(Response response) {
        response.then().assertThat()
                .statusCode(401)
                .and()
                .body("success", is(false))
                .and()
                .body("message", equalTo("You should be authorised"));
    }

    public void testNegative500(Response response) {
        response.then().assertThat()
                .statusCode(500);
    }
}
