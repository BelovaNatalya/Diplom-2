package client;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OrderClient extends ApiClient {
    private static final String ORDERS_ENDPOINT = "orders";

    @Step("Создание заказа с токеном")
    public Response createOrderWithToken(String token, String ingredient) {
        return given()
                .spec(getBaseSpecification())
                .auth().oauth2(token)
                .body(ingredient)
                .post(ORDERS_ENDPOINT);
    }

    @Step("Создание заказа без токена")
    public Response createOrderWithoutToken(String ingredient) {
        return given()
                .spec(getBaseSpecification())
                .body(ingredient)
                .post(ORDERS_ENDPOINT);
    }

    @Step("Получение списка заказов пользователя с токеном")
    public Response getUserOrdersListWithToken(String token) {
        return given()
                .spec(getBaseSpecification())
                .auth().oauth2(token)
                .get(ORDERS_ENDPOINT);
    }

    @Step("Получение списка заказов пользователя без токена")
    public Response getUserOrdersListWithoutToken() {
        return given()
                .spec(getBaseSpecification())
                .get(ORDERS_ENDPOINT);
    }

}
