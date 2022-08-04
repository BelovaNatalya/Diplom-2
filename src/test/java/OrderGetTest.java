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

public class OrderGetTest {
    User user;
    OrderClient orderClient;
    UserClient userClient;
    String authToken;
    String correctIngredients = "{\n\"ingredients\": [\"61c0c5a71d1f82001bdaaa75\",\"61c0c5a71d1f82001bdaaa70\"]\n}";

    private final String UNAUTHORIZED_ERROR_MESSAGE = "You should be authorised";

    @Before
    public void setUp() {
        user = User.generateUser();
        orderClient = new OrderClient();
        userClient = new UserClient();

        Response createUserResponse = userClient.createNewUser(user);
        authToken = createUserResponse.path("accessToken");
        orderClient.createOrderWithToken(authToken.substring(7), correctIngredients);
    }

    @After
    public void cleanUp() {
        if (authToken != null) {
            userClient.deleteUser(authToken.substring(7));
        }
    }

    @Test
    @DisplayName("Получение заказов пользователя с авторизацией")
    public void shouldBePossibleToGetOrdersFromUserWithAuth(){
        Response response = orderClient.getUserOrdersListWithToken(authToken.substring(7));

        assertThat("Ошибка при получении списка заказов: тело ответа не содержит параметра success со значением true", response.path("success"), equalTo(true));
        //про статус ответа в документации ничего не сказано, но по сути, так как это get-запрос должен быть 200
        assertThat("Ошибка при получении списка заказов: вернулся код ответа, отличный от ожидаемого 200 success", response.statusCode(), equalTo(SC_OK));
    }

    @Test
    @DisplayName("Получение заказов пользователя без авторизации")
    public void shouldBeImpossibleToGetOrdersFromUserWithoutAuth(){
        Response response = orderClient.getUserOrdersListWithoutToken();

        assertThat("Ошибка при получении списка заказов: тело ответа содержит номер заказа", response.path("order.number"), nullValue());
        assertThat("Ошибка при получении списка заказов: тело ответа не содержит параметра success со значением false", response.path("success"), equalTo(false));
        assertThat("Ошибка при получении списка заказов: вернулся код ответа, отличный от ожидаемого 401 unauthorized", response.statusCode(), equalTo(SC_UNAUTHORIZED));
        assertThat("Ошибка при получении списка заказов: вернулось сообщение, не соответствующее ожидаемому You should be authorised", response.path("message"), equalTo(UNAUTHORIZED_ERROR_MESSAGE));
    }
}
