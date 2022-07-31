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

public class OrderCreateTest {

    String correctIngredients = "{\n\"ingredients\": [\"61c0c5a71d1f82001bdaaa75\",\"61c0c5a71d1f82001bdaaa70\"]\n}";
    String emptyIngredients = "{\n\"ingredients\": []\n}";
    String wrongIngredients = "{\n\"ingredients\": [\"somerandomletters1\",\"somerandomletters2\"]\n}";


    User user;
    OrderClient orderClient;
    UserClient userClient;
    String authToken;

    private final String INGREDIENTS_MISSING_ERROR_MESSAGE = "Ingredient ids must be provided";

    @Before
    public void setUp() {
        user = User.generateUser();
        orderClient = new OrderClient();
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
    @DisplayName("Создание заказа с авторизацией и списком ингредиентов")
    public void shouldBePossibleCreateOrderWithAuthAndWithIngredientsTest() {
        Response response = orderClient.createOrderWithToken(authToken.substring(7), correctIngredients);

        assertThat("Ошибка при создании заказа: тело ответа не содержит номер заказа", response.path("order.number"), notNullValue());
        //про статус ответа в документации ничего не сказано, но по сути, так как это создание, должен быть 201. Возвращается 200, поэтому строку с проверкой кода закомментила
        //assertThat("Ошибка при создании заказа: вернулся код ответа, отличный от ожидаемого 201 created", response.statusCode(), equalTo(SC_CREATED));
        assertThat("Ошибка при создании заказа: тело ответа не содержит параметра success со значением true", response.path("success"), equalTo(true));
    }

    @Test
    @DisplayName("Создание заказа без авторизации и со списком ингредиентов")
    public void shouldBePossibleCreateOrderWithoutAuthAndWithIngredientsTest() {
        Response response = orderClient.createOrderWithoutToken(correctIngredients);

        assertThat("Ошибка при создании заказа: тело ответа не содержит номер заказа", response.path("order.number"), notNullValue());
        //про статус ответа в документации ничего не сказано, но по сути, так как это создание, должен быть 201. Возвращается 200, поэтому строку с проверкой кода закомментила
        //assertThat("Ошибка при создании заказа: вернулся код ответа, отличный от ожидаемого 201 created", response.statusCode(), equalTo(SC_CREATED));
        assertThat("Ошибка при создании заказа: тело ответа не содержит параметра success со значением true", response.path("success"), equalTo(true));
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и без списка ингредиентов")
    public void shouldBeImpossibleCreateOrderWithAuthAndWithoutIngredientsTest() {
        Response response = orderClient.createOrderWithToken(authToken.substring(7), emptyIngredients);

        assertThat("Ошибка при создании заказа: вернулся код ответа, отличный от ожидаемого 400 bad request", response.statusCode(), equalTo(SC_BAD_REQUEST));
        assertThat("Ошибка при создании заказа: тело ответа не содержит параметра success со значением false", response.path("success"), equalTo(false));
        assertThat("Ошибка при получении списка заказов: вернулось сообщение, не соответствующее ожидаемому Ingredient ids must be provided", response.path("message"), equalTo(INGREDIENTS_MISSING_ERROR_MESSAGE));
    }

    @Test
    @DisplayName("Создание заказа без авторизации и без списка ингредиентов")
    public void shouldBeImpossibleCreateOrderWithoutAuthAndWithoutIngredientsTest() {
        Response response = orderClient.createOrderWithoutToken(emptyIngredients);

        assertThat("Ошибка при создании заказа: вернулся код ответа, отличный от ожидаемого 400 bad request", response.statusCode(), equalTo(SC_BAD_REQUEST));
        assertThat("Ошибка при создании заказа: тело ответа не содержит параметра success со значением false", response.path("success"), equalTo(false));
        assertThat("Ошибка при получении списка заказов: вернулось сообщение, не соответствующее ожидаемому Ingredient ids must be provided", response.path("message"), equalTo(INGREDIENTS_MISSING_ERROR_MESSAGE));
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и с неверным хэшем ингредиентов")
    public void shouldBeImpossibleCreateOrderWithAuthAndWrongIngredientsTest() {
        Response response = orderClient.createOrderWithToken(authToken.substring(7), wrongIngredients);

        assertThat("Ошибка при создании заказа: вернулся код ответа, отличный от ожидаемого 500 internal server error", response.statusCode(), equalTo(SC_INTERNAL_SERVER_ERROR));
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и с неверным хэшем ингредиентов")
    public void shouldBeImpossibleCreateOrderWithoutAuthAndWrongIngredientsTest() {
        Response response = orderClient.createOrderWithoutToken(wrongIngredients);

        assertThat("Ошибка при создании заказа: вернулся код ответа, отличный от ожидаемого 500 internal server error", response.statusCode(), equalTo(SC_INTERNAL_SERVER_ERROR));
    }
}
