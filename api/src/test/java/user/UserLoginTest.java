package user;

import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UserLoginTest {

    private boolean userAuthorisationSuccess;
    private String userAccessToken;

    User user;

    //создание учетной записи пользователя
    @Before
    public void tearUp() throws Exception {
        user = user.getRandomUser();
        userAccessToken = UserSpec.getResponseCreateUser(user,200).accessToken;
    }

    //удаление учетной записи пользователя
    @After
    public void tearDown() throws Exception {
        UserSpec.getResponseUserDeleted(userAccessToken, 202);
    }

    @Test
    @DisplayName("Тест успешной авторизации под существующим пользователем")
    public void successfulAuthorizationUserTestOk() throws JsonProcessingException {
        //данные для авторизации существующего пользователя
        User createdUser = new User(user.getEmail(), user.getPassword());
        //авторизация пользователя
        UserSpec response = UserSpec.getResponseUserAuthorization(createdUser, 200);
        userAccessToken = response.accessToken;
        userAuthorisationSuccess = response.success;
        assertTrue(userAuthorisationSuccess);
    }

    @Test
    @DisplayName("Тест неуспешной авторизации под существующим пользователем с неверным логином (email)")
    public void failAuthorizationUserWithInvalidEmailTestOk() throws JsonProcessingException {
        String invalidEmail = "Invalid" + user.getEmail();
        //данные для авторизации существующего пользователя
        User createdUser = new User(invalidEmail, user.getPassword());
        //авторизация пользователя
        userAuthorisationSuccess = UserSpec.getResponseUserAuthorization(createdUser, 401).success;
        assertFalse(userAuthorisationSuccess);
    }

    @Test
    @DisplayName("Тест неуспешной авторизации под существующим пользователем с неверным паролем")
    public void failAuthorizationUserWithInvalidPasswordTestOk() throws JsonProcessingException {
        String invalidPassword = "Invalid" + user.getPassword();
        //данные для авторизации существующего пользователя
        User createdUser = new User(user.getEmail(), invalidPassword);
        //авторизация пользователя
        userAuthorisationSuccess =  UserSpec.getResponseUserAuthorization(createdUser, 401).success;
        assertFalse(userAuthorisationSuccess);
    }
}
