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

    User user = new User();

    //удаление учетной записи пользователя
    @After
    public void tearDown() throws Exception {
        if (userCreateSuccess) {
            UserSpec.getResponseUserDeleted(userAccessToken, 202);
        }
    }

    @Test
    @DisplayName("Тест успешного создания учетной записи пользователя")
    public void successfulCreateUserTestOk() throws JsonProcessingException {
        //создание пользователя
        user = User.getRandomUser();
        //создание учетки пользователя
        UserSpec.getResponseCreateUser(user,200);
        userAccessToken = UserSpec.accessToken;
        userCreateSuccess = UserSpec.success;
        assertThat(userAccessToken, notNullValue());
        assertTrue(userCreateSuccess);
    }

    @Test
    @DisplayName("Тест неуспешного создания учетной записи пользователя без пароля")
    public void failCreateUserWithOutPasswordTestOk() throws JsonProcessingException {
        //создание пользователя без пароля
        user = User.getRandomUserWithoutPassword();
        //создание учетки пользователя
        UserSpec.getResponseCreateUser(user, 403);
        assertFalse(UserSpec.success);
        assertEquals("Email, password and name are required fields",UserSpec.message);
    }

    @Test
    @DisplayName("Тест неуспешного создания учетной записи пользователя без имени")
    public void failCreateUserWithOutNameTestOk() throws JsonProcessingException {
        //создание пользователя без имени
        user = User.getRandomUserWithoutName();
        //создание "учетки" пользователя
        UserSpec.getResponseCreateUser(user, 403);
        assertFalse(UserSpec.success);
        assertEquals("Email, password and name are required fields", UserSpec.message);
    }

    @Test
    @DisplayName("Тест неуспешного создания учетной записи пользователя без email")
    public void failCreateUserWithOutEmailTestOk() throws JsonProcessingException {
        //создание пользователя без email
        user = User.getRandomUserWithoutEmail();
        //создание учетки пользователя
        UserSpec.getResponseCreateUser(user, 403);
        assertFalse(UserSpec.success);
        assertEquals("Email, password and name are required fields",UserSpec.message);
    }

    @Test
    @DisplayName("Тест неуспешного создания учетной записи " +
            "пользователя который уже зарегистрирован (с повторяющимся email)")
    public void failCreateCourierRecurringEmailTestOk() throws JsonProcessingException {
        //создание пользователя
        user = User.getRandomUser();
        //создание учетки пользователя
        UserSpec.getResponseCreateUser(user,200);
        userAccessToken = UserSpec.accessToken;
        userCreateSuccess = UserSpec.success;
        //создание учетки пользователя который уже зарегистрирован
        UserSpec.getResponseCreateUser(user, 403);
        assertFalse(UserSpec.success);
        assertEquals("User already exists",UserSpec.message);
    }
}