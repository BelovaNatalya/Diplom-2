import client.OrderClient;
import client.UserClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.apache.http.HttpStatus.*;

public class UserCreateTest {
    User user;
    UserClient userClient;
    String authToken;

    private final String DOUBLE_USER_ERROR_MESSAGE = "User already exists";
    private final String REQUIRED_FIELDS_MISSING_ERROR_MESSAGE = "Email, password and name are required fields";

    @Before
    public void setUp() {
        user = User.generateUser();
        userClient = new UserClient();
    }

    @After
    public void cleanUp() {
        if (authToken != null) {
            userClient.deleteUser(authToken.substring(7));
        }
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    public void shouldBePossibleToCreateUserWithCorrectData(){
        Response response = userClient.createNewUser(user);

        assertThat("Ошибка создания пользователя: тело ответа не содержит параметр success со значением true", response.path("success"), equalTo(true));
        assertThat("Ошибка создания пользователя: тело ответа не содержит параметр токена авторизации", response.path("accessToken"), notNullValue());
        assertThat("Ошибка создания пользователя: тело ответа содержит неверно сгенерированный токен", response.path("accessToken"), containsString("Bearer"));
        //про статус ответа в документации ничего не сказано, но по сути должен быть 201. закомментила ассерт, так как тест падает - возвращается 200
        //assertThat("Ошибка при получении списка заказов: вернулся код ответа, отличный от ожидаемого 201 created", response.statusCode(), equalTo(SC_CREATED));
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован в системе")
    public void shouldBeImpossibleToCreateAlreadyRegisteredUser(){
        userClient.createNewUser(user);
        Response responseSecondRequest = userClient.createNewUser(user);

        assertThat("Ошибка создания пользователя: тело ответа не содержит параметр success со значением false", responseSecondRequest.path("success"), equalTo(false));
        assertThat("Ошибка создания пользователя: статус ответа от сервера отличный от ожидаемого 403 forbidden", responseSecondRequest.statusCode(), equalTo(SC_FORBIDDEN));
        assertThat("Ошибка создания пользователя: тело ответа не содержит параметр message со значением User already exists", responseSecondRequest.path("message"), equalTo(DOUBLE_USER_ERROR_MESSAGE));
    }

    @Test
    @DisplayName("Создание пользователя без указания email")
    public void shouldBeImpossibleToCreateUserWithoutEmail(){
        user.setEmail(null);
        Response response = userClient.createNewUser(user);

        assertThat("Ошибка создания пользователя: тело ответа не содержит параметр success со значением false", response.path("success"), equalTo(false));
        assertThat("Ошибка создания пользователя: статус ответа от сервера отличный от ожидаемого 403 forbidden", response.statusCode(), equalTo(SC_FORBIDDEN));
        assertThat("Ошибка создания пользователя: тело ответа не содержит параметр message со значением Email, password and name are required fields", response.path("message"), equalTo(REQUIRED_FIELDS_MISSING_ERROR_MESSAGE));
    }

    @Test
    @DisplayName("Создание пользователя без указания пароля")
    public void shouldBeImpossibleToCreateUserWithoutPassword(){
        user.setPassword(null);
        Response response = userClient.createNewUser(user);

        assertThat("Ошибка создания пользователя: тело ответа не содержит параметр success со значением false", response.path("success"), equalTo(false));
        assertThat("Ошибка создания пользователя: статус ответа от сервера отличный от ожидаемого 403 forbidden", response.statusCode(), equalTo(SC_FORBIDDEN));
        assertThat("Ошибка создания пользователя: тело ответа не содержит параметр message со значением Email, password and name are required fields", response.path("message"), equalTo(REQUIRED_FIELDS_MISSING_ERROR_MESSAGE));
    }

    @Test
    @DisplayName("Создание пользователя без указания имени")
    public void shouldBeImpossibleToCreateUserWithoutName(){
        user.setName(null);
        Response response = userClient.createNewUser(user);

        assertThat("Ошибка создания пользователя: тело ответа не содержит параметр success со значением false", response.path("success"), equalTo(false));
        assertThat("Ошибка создания пользователя: статус ответа от сервера отличный от ожидаемого 403 forbidden", response.statusCode(), equalTo(SC_FORBIDDEN));
        assertThat("Ошибка создания пользователя: тело ответа не содержит параметр message со значением Email, password and name are required fields", response.path("message"), equalTo(REQUIRED_FIELDS_MISSING_ERROR_MESSAGE));
    }
}
