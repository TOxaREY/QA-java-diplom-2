package src.main.java.api.client;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;

public class UserClient {

    public Response responseCreateUser(File json) {
        return given().header("Content-type", "application/json")
                .and()
                .body(json)
                .when()
                .post("/api/auth/register");
    }

    public Response responseAuthorizationUser(File json, String path) {
        return given().header("Content-type", "application/json")
                .and()
                .body(json)
                .when()
                .post("/api/auth/" + path);
    }

    public Response responseUpdateWithAuthorization(File json, String accessToken) {
        return given().header("Content-type", "application/json")
                .and()
                .header("Authorization", accessToken)
                .and()
                .body(json)
                .when()
                .patch("/api/auth/user");
    }

    public Response responseUpdateWithoutAuthorization(File json) {
        return given().header("Content-type", "application/json")
                .and()
                .body(json)
                .when()
                .patch("/api/auth/user");
    }

    public String setAccessToken(Response response) {
        JsonPath jsonPathEvaluator = response.jsonPath();
        return jsonPathEvaluator.get("accessToken").toString();
    }

    public void testNegative403(Response response, String message) {
        response.then().assertThat()
                .statusCode(403)
                .and()
                .body("success", is(false))
                .and()
                .body("message", equalTo(message));
    }

    public void testNegative401(Response response) {
        response.then().assertThat()
                .statusCode(401)
                .and()
                .body("success", is(false))
                .and()
                .body("message", equalTo("email or password are incorrect"));
    }

    public void testNegative401Author(Response response) {
        response.then().assertThat()
                .statusCode(401)
                .and()
                .body("success", is(false))
                .and()
                .body("message", equalTo("You should be authorised"));
    }
}
