package client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.User;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;

public class UserClient extends ApiClient {
    private static final String USER_CREATE_ENDPOINT = "auth/register";
    private static final String USER_DATA_ENDPOINT = "auth/user";
    private static final String USER_LOGIN_ENDPOINT = "auth/login";

    @Step("Создание нового пользователя")
    public Response createNewUser(User user) {
        return given()
                .spec(getBaseSpecification())
                .body(user)
                .post(USER_CREATE_ENDPOINT);
    }

    @Step("Удаление пользователя")
    public Response deleteUser(String token) {
        return given()
                .spec(getBaseSpecification())
                .auth().oauth2(token)
                .delete(USER_DATA_ENDPOINT);
    }

    @Step("Вход пользователя")
    public Response loginUser(User user) {
        return given()
                .spec(getBaseSpecification())
                .body(user)
                .post(USER_LOGIN_ENDPOINT);
    }

    @Step("Изменение данных о пользователе с токеном")
    public Response changeUserDataWithToken(String token, User user) {
        return given()
                .spec(getBaseSpecification())
                .auth().oauth2(token)
                .body(user)
                .patch(USER_DATA_ENDPOINT);
    }

    @Step("Изменение данных о пользователе без токена")
    public Response changeUserDataWithoutToken(User user) {
        return given()
                .spec(getBaseSpecification())
                .body(user)
                .patch(USER_DATA_ENDPOINT);
    }

}
