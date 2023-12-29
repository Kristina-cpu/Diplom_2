package order;

import user.User;
import user.UserSpec;
import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;

public class OrderListTest {
    private String userAccessToken;
    private int numberOfOrders;

    User user = new User();

    @Before
    public void tearUp() throws Exception {
        //создание пользователя
        user = User.getRandomUser();
        UserSpec.getResponseCreateUser(user,200);
        userAccessToken = UserSpec.accessToken;
        //количество заказов пользователя
        numberOfOrders = 4;
        //создание списка заказов пользователя
        OrderSpec.createListOfOrders(user, numberOfOrders);
    }

    @After //удаление учетной записи пользователя
    public void tearDown() {
        UserSpec.getResponseUserDeleted(userAccessToken, 202);
    }

    @Test
    @DisplayName("Тест успешного получения списка заказов авторизованного пользователя")
    public void successfulGetOfOrdersListFromAuthorizedUserTestOk() throws JsonProcessingException {
        //авторизацию пользователя
        UserSpec.getResponseUserAuthorization(user, 200);
        userAccessToken = UserSpec.accessToken;
        //получения списка заказов пользователя
        ArrayList<Integer> orderNumber =
                new ArrayList<>(OrderSpec.getAnOrderListRequestResponse(userAccessToken, 200)
                        .extract()
                        .path("orders.number"));
        assertEquals(numberOfOrders, orderNumber.size());
    }

    @Test
    @DisplayName("Тест неуспешного получения списка заказов неавторизованного пользователя")
    public void failGetOfOrdersListFromUnauthorizedUserTestOk() throws JsonProcessingException {
        //получения списка заказов пользователя
        OrderSpec.getAnOrderListRequestResponse("", 401)
                .body("message",equalTo("You should be authorised"));
    }
}
