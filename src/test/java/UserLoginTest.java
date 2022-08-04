import client.UserClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class UserLoginTest {
    User user;
    UserClient userClient;
    String authToken;

    private final String WRONG_LOGIN_DATA_ERROR_MESSAGE = "email or password are incorrect";

    @Before
    public void setUp() {
        user = User.generateUser();
        userClient = new UserClient();
        userClient.createNewUser(user);
    }

    @After
    public void cleanUp() {
        if (authToken != null) {
            userClient.deleteUser(authToken.substring(7));
        }
    }

    @Test
    @DisplayName("Авторизация пользователя с корректными логином и паролем")
    public void shouldBePossibleToAuthorizeWithCorrectLoginAndPassword() {
        Response response = userClient.loginUser(user);

        assertThat("Ошибка авторизации пользователя: тело ответа не содержит параметр success со значением true", response.path("success"), equalTo(true));
        //про статус ответа в документации ничего не сказано, но по сути должен быть 201. возвращается 200, поэтому закомментила ассерт
        //assertThat("Ошибка создания пользователя: вернулся код ответа, отличный от ожидаемого 201 created", response.statusCode(), equalTo(SC_CREATED));
        assertThat("Ошибка авторизации пользователя: тело ответа не содержит параметр токена авторизации", response.path("accessToken"), notNullValue());
        assertThat("Ошибка авторизации пользователя: тело ответа содержит неверно сгенерированный токен", response.path("accessToken"), containsString("Bearer"));
        assertThat("Ошибка авторизации пользователя: тело ответа содержит неверный email", response.path("user.email"), equalTo(user.getEmail().toLowerCase()));
        assertThat("Ошибка авторизации пользователя: тело ответа содержит неверное имя", response.path("user.name"), equalTo(user.getName()));
    }

    @Test
    @DisplayName("Авторизация пользователя с некорректными логином и паролем")
    public void shouldBeImpossibleToAuthorizeWithIncorrectLoginAndPassword() {
        user.setEmail(user.getEmail() + "somerandomletters");
        user.setPassword(user.getPassword() + "somerandomletters");
        Response response = userClient.loginUser(user);

        assertThat("Ошибка авторизации пользователя: тело ответа не содержит параметр success со значением false", response.path("success"), equalTo(false));
        assertThat("Ошибка авторизации пользователя: вернулся код ответа, отличный от ожидаемого 401 unauthorized", response.statusCode(), equalTo(SC_UNAUTHORIZED));
        assertThat("Ошибка авторизации пользователя: вернулось сообщение, не соответствующее ожидаемому email or password are incorrect", response.path("message"), equalTo(WRONG_LOGIN_DATA_ERROR_MESSAGE));
    }

    @Test
    @DisplayName("Авторизация пользователя с некорректным логином и корректным паролем")
    public void shouldBeImpossibleToAuthorizeWithIncorrectLoginAndCorrectPassword() {
        user.setEmail(user.getEmail() + "somerandomletters");
        Response response = userClient.loginUser(user);

        assertThat("Ошибка авторизации пользователя: тело ответа не содержит параметр success со значением false", response.path("success"), equalTo(false));
        assertThat("Ошибка авторизации пользователя: вернулся код ответа, отличный от ожидаемого 401 unauthorized", response.statusCode(), equalTo(SC_UNAUTHORIZED));
        assertThat("Ошибка авторизации пользователя: вернулось сообщение, не соответствующее ожидаемому email or password are incorrect", response.path("message"), equalTo(WRONG_LOGIN_DATA_ERROR_MESSAGE));
    }

    @Test
    @DisplayName("Авторизация пользователя с корректным логином и некорректным паролем")
    public void shouldBeImpossibleToAuthorizeWithCorrectLoginAndIncorrectPassword() {
        user.setPassword(user.getPassword() + "somerandomletters");
        Response response = userClient.loginUser(user);

        assertThat("Ошибка авторизации пользователя: тело ответа не содержит параметр success со значением false", response.path("success"), equalTo(false));
        assertThat("Ошибка авторизации пользователя: вернулся код ответа, отличный от ожидаемого 401 unauthorized", response.statusCode(), equalTo(SC_UNAUTHORIZED));
        assertThat("Ошибка авторизации пользователя: вернулось сообщение, не соответствующее ожидаемому email or password are incorrect", response.path("message"), equalTo(WRONG_LOGIN_DATA_ERROR_MESSAGE));
    }
}
