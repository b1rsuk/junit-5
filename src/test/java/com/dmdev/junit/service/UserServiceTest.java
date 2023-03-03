package com.dmdev.junit.service;


import com.dmdev.junit.TestBase;
import com.dmdev.junit.extension.GlobalExtension;
import com.dmdev.junit.extension.PostProcessingExtension;
import com.dmdev.junit.extension.ThrowableExtension;
import com.dmdev.junit.extension.UserServiceParamResolver;
import org.assertj.core.api.Assertions;
import org.example.UserService;
import org.example.dto.User;
import org.example.dto.UserDao;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith({
        UserServiceParamResolver.class,
        PostProcessingExtension.class,
      //  ThrowableExtension.class
})
class UserServiceTest extends TestBase {
    private UserDao userDao;
    private UserService userService;

    UserServiceTest(TestInfo testInfo) {
        System.out.println();
    }
    private static final User IVAN = User.of(1, "Ivan", "123");
    private static final User PETR = User.of(2, "Petr", "111");
    @BeforeAll
    void init() {
        System.out.println("Before all: " + this);
    }
    @BeforeEach
    void prepare() {
        this.userDao = Mockito.mock(UserDao.class);
        this.userService = new UserService(userDao);
    }

    @Test
    void shouldDeleteExistedUser() {
        userService.add(IVAN);
        Mockito.doReturn(true).when(userDao).delete(IVAN.getId());
        Boolean deleteResult = userService.delete(IVAN.getId());
        Assertions.assertThat(deleteResult).isTrue();
    }

    @Test
    void userEmptyIfNoUserAdded() {
        List<User> userList = userService.getAll();
        Assertions.assertThat(userList.isEmpty()).isTrue();
    }

    @Test
    void userSizeIfUserAdded() {
        userService.add(IVAN, PETR);
        List<User> userList = userService.getAll();
        Assertions.assertThat(userList).hasSize(2);
    }

    @Test
    @Tag("login")
    void loginSuccessIfUserExist() {
        userService.add(IVAN);
        Optional<User> maybeUser = userService.login(IVAN.getUsername(), IVAN.getPassword());
        Assertions.assertThat(maybeUser.isPresent()).isTrue();
        maybeUser.ifPresent(user -> Assertions.assertThat(user).isEqualTo(IVAN));
    }

    @Test
    void usersConvertedToMapById() {
        userService.add(IVAN, PETR);
        Map<Integer, User> userMap = userService.getAllConvertedById();
        assertAll(
                () -> Assertions.assertThat(userMap).containsKeys(IVAN.getId(), PETR.getId()),
                () -> Assertions.assertThat(userMap).containsValues(IVAN, PETR)
        );
    }

    @Test
    @Tag("login")
    void loginFailIfPasswordIsNotCorrect() {
        userService.add(IVAN);
        Optional<User> maybeUser = userService.login(IVAN.getUsername(), "fail");
        Assertions.assertThat(maybeUser.isEmpty()).isTrue();
    }

    @Test
    @Tag("login")
    void throwExceptionIfUsernameOrPasswordIsNull() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> userService.login(null, "dummy")),
                () -> assertThrows(IllegalArgumentException.class, () -> userService.login("dummy", null)),
                () -> assertThrows(IllegalArgumentException.class, () -> userService.login(null, null))
        );
    }

    @Test
    void loginFailIfUserIsNotCorrect() {
        userService.add(IVAN);
        Optional<User> maybeUser = userService.login("fail", IVAN.getPassword());
        Assertions.assertThat(maybeUser.isEmpty()).isTrue();
    }

    @AfterEach
    void deleteDataFromDatabase() {
        System.out.println("After Each: " + this);
    }

    @AfterAll
    void closeConnectionPool() {
        System.out.println("Before all " + this);
    }
}
