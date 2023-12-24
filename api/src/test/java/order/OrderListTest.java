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

    User user;
    UserSpec userSpec;
    OrderSpec orderSpec;

    @Before
    public void tearUp() throws Exception {
        //создание пользователя
        user = user.getRandomUser();

        userAccessToken = userSpec.getResponseCreateUser(user,200).accessToken;
        //количество заказов пользователя
        numberOfOrders = 4;
        //создание списка заказов пользователя
        orderSpec.createListOfOrders(user, numberOfOrders);
    }

    @After //удаление учетной записи пользователя
    public void tearDown() throws Exception {
        userSpec.getResponseUserDeleted(userAccessToken, 202);
    }

    @Test
    @DisplayName("Тест успешного получения списка заказов авторизованного пользователя")
    public void successfulGetOfOrdersListFromAuthorizedUserTestOk() throws JsonProcessingException {
        //авторизацию пользователя
        userAccessToken = userSpec.getResponseUserAuthorization(user, 200).accessToken;
        //получения списка заказов пользователя
        ArrayList<Integer> orderNumber =
                new ArrayList<>(orderSpec.getAnOrderListRequestResponse(userAccessToken, 200)
                .extract()
                .path("orders.number"));
        assertEquals(numberOfOrders, orderNumber.size());
    }

    @Test
    @DisplayName("Тест неуспешного получения списка заказов неавторизованного пользователя")
    public void failGetOfOrdersListFromUnauthorizedUserTestOk() throws JsonProcessingException {
        //получения списка заказов пользователя
        orderSpec.getAnOrderListRequestResponse("", 401)
                .body("message",equalTo("You should be authorised"));
    }
}
