package user;

import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class UserCreateTest {

    private String userAccessToken;
    private boolean userCreateSuccess;

    User user;
    UserSpec userSpec;

    //удаление учетной записи пользователя
    @After
    public void tearDown() throws Exception {
        if (userCreateSuccess) {
            userSpec.getResponseUserDeleted(userAccessToken, 202);
        }
    }

    @Test
    @DisplayName("Тест успешного создания учетной записи пользователя")
    public void successfulCreateUserTestOk() throws JsonProcessingException {
        //создание пользователя
        user = user.getRandomUser();
        //создание учетки пользователя
        UserSpec response = userSpec.getResponseCreateUser(user,200);
        userAccessToken = response.accessToken;
        userCreateSuccess = response.success;
        assertThat(userAccessToken, notNullValue());
        assertTrue(userCreateSuccess);
    }

    @Test
    @DisplayName("Тест неуспешного создания учетной записи пользователя без пароля")
    public void failCreateUserWithOutPasswordTestOk() throws JsonProcessingException {
        //создание пользователя без пароля
        user = user.getRandomUserWithoutPassword();
        //создание учетки пользователя
        UserSpec response = userSpec.getResponseCreateUser(user, 403);
        assertFalse(response.success);
        assertEquals("Email, password and name are required fields",response.message);
    }

    @Test
    @DisplayName("Тест неуспешного создания учетной записи пользователя без имени")
    public void failCreateUserWithOutNameTestOk() throws JsonProcessingException {
        //создание пользователя без имени
        user = user.getRandomUserWithoutName();
        //создание "учетки" пользователя
        UserSpec response = userSpec.getResponseCreateUser(user, 403);
        assertFalse(response.success);
        assertEquals("Email, password and name are required fields",response.message);
    }

    @Test
    @DisplayName("Тест неуспешного создания учетной записи пользователя без email")
    public void failCreateUserWithOutEmailTestOk() throws JsonProcessingException {
        //создание пользователя без email
        user = user.getRandomUserWithoutEmail();
        //создание учетки пользователя
        UserSpec response = userSpec.getResponseCreateUser(user, 403);
        assertFalse(response.success);
        assertEquals("Email, password and name are required fields",response.message);
    }

    @Test
    @DisplayName("Тест неуспешного создания учетной записи " +
            "пользователя который уже зарегистрирован (с повторяющимся email)")
    public void failCreateCourierRecurringEmailTestOk() throws JsonProcessingException {
        //создание пользователя
        user = user.getRandomUser();
        //создание учетки пользователя
        UserSpec initResponse = userSpec.getResponseCreateUser(user,200);
        userAccessToken = initResponse.accessToken;
        userCreateSuccess = initResponse.success;
        //создание учетки пользователя который уже зарегистрирован
        UserSpec response = userSpec.getResponseCreateUser(user, 403);
        assertFalse(response.success);
        assertEquals("User already exists",response.message);
    }
}