import client.UserClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.apache.http.HttpStatus.*;

public class UserUpdateDataTest {
    User user;
    UserClient userClient;
    String authToken;

    private final String UNAUTHORIZED_ERROR_MESSAGE = "You should be authorised";
    private final String DOUBLE_EMAIL_ERROR_MESSAGE = "User with such email already exists";

    @Before
    public void setUp() {
        user = User.generateUser();
        userClient = new UserClient();
        Response createUserResponse = userClient.createNewUser(user);
        authToken = createUserResponse.path("accessToken");
    }

    @After
    public void cleanUp() {
        if (authToken != null) {
            userClient.deleteUser(authToken.substring(7));
        }
    }

    @Test
    @DisplayName("Изменение всех данных пользователя с авторизацией")
    public void shouldBePossibleToChangeAuthorizedUserData() {
        user.setEmail(user.getEmail() + "somerandomletters");
        user.setPassword(user.getPassword() + "somerandomletters");
        user.setName(user.getName() + "somerandomletters");

        Response response = userClient.changeUserDataWithToken(authToken.substring(7), user);

        assertThat("Ошибка обновления данных пользователя: тело ответа не содержит параметр success со значением true", response.path("success"), equalTo(true));
        //про статус ответа в документации ничего не сказано, но по сути должен быть 200
        assertThat("Ошибка обновления данных пользователя: вернулся код ответа, отличный от ожидаемого 200 success", response.statusCode(), equalTo(SC_OK));
        assertThat("Ошибка обновления данных пользователя: тело ответа содержит неверный email", response.path("user.email"), equalTo(user.getEmail().toLowerCase()));
        assertThat("Ошибка обновления данных пользователя: тело ответа содержит неверное имя", response.path("user.name"), equalTo(user.getName()));

    }

    @Test
    @DisplayName("Изменение email пользователя с авторизацией")
    public void shouldBePossibleToChangeAuthorizedUserEmail() {
        user.setEmail(user.getEmail() + "somerandomletters");

        Response response = userClient.changeUserDataWithToken(authToken.substring(7), user);

        assertThat("Ошибка обновления данных пользователя: тело ответа не содержит параметр success со значением true", response.path("success"), equalTo(true));
        //про статус ответа в документации ничего не сказано, но по сути должен быть 200
        assertThat("Ошибка обновления данных пользователя: вернулся код ответа, отличный от ожидаемого 200 success", response.statusCode(), equalTo(SC_OK));
        assertThat("Ошибка обновления данных пользователя: тело ответа содержит неверный email", response.path("user.email"), equalTo(user.getEmail().toLowerCase()));
    }

    @Test
    @DisplayName("Изменение пароля пользователя с авторизацией")
    public void shouldBePossibleToChangeAuthorizedUserPassword() {
        user.setPassword(user.getPassword() + "somerandomletters");

        Response response = userClient.changeUserDataWithToken(authToken.substring(7), user);

        assertThat("Ошибка обновления данных пользователя: тело ответа не содержит параметр success со значением true", response.path("success"), equalTo(true));
        //про статус ответа в документации ничего не сказано, но по сути должен быть 200
        assertThat("Ошибка обновления данных пользователя: вернулся код ответа, отличный от ожидаемого 200 success", response.statusCode(), equalTo(SC_OK));
        assertThat("Ошибка обновления данных пользователя: тело ответа содержит неверный email", response.path("user.email"), equalTo(user.getEmail().toLowerCase()));
    }

    @Test
    @DisplayName("Изменение имени пользователя с авторизацией")
    public void shouldBePossibleToChangeAuthorizedUserName() {
        user.setName(user.getName() + "somerandomletters");

        Response response = userClient.changeUserDataWithToken(authToken.substring(7), user);

        assertThat("Ошибка обновления данных пользователя: тело ответа не содержит параметр success со значением true", response.path("success"), equalTo(true));
        //про статус ответа в документации ничего не сказано, но по сути должен быть 200
        assertThat("Ошибка обновления данных пользователя: вернулся код ответа, отличный от ожидаемого 200 success", response.statusCode(), equalTo(SC_OK));
        assertThat("Ошибка обновления данных пользователя: тело ответа содержит неверное имя", response.path("user.name"), equalTo(user.getName()));
    }

    @Test
    @DisplayName("Изменение всех данных пользователя без авторизации")
    public void shouldBeImpossibleToChangeUnauthorizedUserData() {
        user.setEmail(user.getEmail() + "somerandomletters");
        user.setPassword(user.getPassword() + "somerandomletters");
        user.setName(user.getName() + "somerandomletters");

        Response response = userClient.changeUserDataWithoutToken(user);

        assertThat("Ошибка обновления данных пользователя: тело ответа не содержит параметр success со значением false", response.path("success"), equalTo(false));
        assertThat("Ошибка обновления данных пользователя: вернулся код ответа, отличный от ожидаемого 401 unauthorized", response.statusCode(), equalTo(SC_UNAUTHORIZED));
        assertThat("Ошибка обновления данных пользователя: тело ответа содержит текст ошибки, отличный от ожидаемого You should be authorised", response.path("message"), equalTo(UNAUTHORIZED_ERROR_MESSAGE));
    }

    @Test
    @DisplayName("Изменение email пользователя без авторизации")
    public void shouldBeImpossibleToChangeUnauthorizedUserEmail() {
        user.setEmail(user.getEmail() + "somerandomletters");

        Response response = userClient.changeUserDataWithoutToken(user);

        assertThat("Ошибка обновления данных пользователя: тело ответа не содержит параметр success со значением false", response.path("success"), equalTo(false));
        assertThat("Ошибка обновления данных пользователя: вернулся код ответа, отличный от ожидаемого 401 unauthorized", response.statusCode(), equalTo(SC_UNAUTHORIZED));
        assertThat("Ошибка обновления данных пользователя: тело ответа содержит текст ошибки, отличный от ожидаемого You should be authorised", response.path("message"), equalTo(UNAUTHORIZED_ERROR_MESSAGE));
    }

    @Test
    @DisplayName("Изменение пароля пользователя без авторизации")
    public void shouldBeImpossibleToChangeUnauthorizedUserPassword() {
        user.setPassword(user.getPassword() + "somerandomletters");

        Response response = userClient.changeUserDataWithoutToken(user);

        assertThat("Ошибка обновления данных пользователя: тело ответа не содержит параметр success со значением false", response.path("success"), equalTo(false));
        assertThat("Ошибка обновления данных пользователя: вернулся код ответа, отличный от ожидаемого 401 unauthorized", response.statusCode(), equalTo(SC_UNAUTHORIZED));
        assertThat("Ошибка обновления данных пользователя: тело ответа содержит текст ошибки, отличный от ожидаемого You should be authorised", response.path("message"), equalTo(UNAUTHORIZED_ERROR_MESSAGE));
    }

    @Test
    @DisplayName("Изменение имени пользователя без авторизации")
    public void shouldBeImpossibleToChangeUnauthorizedUserName() {
        user.setName(user.getName() + "somerandomletters");

        Response response = userClient.changeUserDataWithoutToken(user);

        assertThat("Ошибка обновления данных пользователя: тело ответа не содержит параметр success со значением false", response.path("success"), equalTo(false));
        assertThat("Ошибка обновления данных пользователя: вернулся код ответа, отличный от ожидаемого 401 unauthorized", response.statusCode(), equalTo(SC_UNAUTHORIZED));
        assertThat("Ошибка обновления данных пользователя: тело ответа содержит текст ошибки, отличный от ожидаемого You should be authorised", response.path("message"), equalTo(UNAUTHORIZED_ERROR_MESSAGE));
    }

    @Test
    @DisplayName("Изменение email на уже существующий в системе")
    public void shouldBeImpossibleToChangeEmailOnAlreadyExisting() {
        User userTwo = User.generateUser();
        userClient.createNewUser(userTwo);
        user.setEmail(userTwo.getEmail());

        Response response = userClient.changeUserDataWithToken(authToken.substring(7), user);

        assertThat("Ошибка обновления данных пользователя: тело ответа не содержит параметр success со значением false", response.path("success"), equalTo(false));
        assertThat("Ошибка обновления данных пользователя: вернулся код ответа, отличный от ожидаемого 403 forbidden", response.statusCode(), equalTo(SC_FORBIDDEN));
        assertThat("Ошибка обновления данных пользователя: тело ответа содержит текст ошибки, отличный от ожидаемого User with such email already exists", response.path("message"), equalTo(DOUBLE_EMAIL_ERROR_MESSAGE));

    }
}