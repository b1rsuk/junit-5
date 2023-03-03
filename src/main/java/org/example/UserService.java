package org.example;

import org.example.dto.User;
import org.example.dto.UserDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import static java.util.stream.Collectors.toMap;

public class UserService {
    private final List<User> userList = new ArrayList<>();
    private final UserDao userDao;

    public boolean delete(Integer userId) {
        return userDao.delete(userId);
    }

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public List<User> getAll() {
        return userList;
    }

    public void add(User ...user) {
        userList.addAll(List.of(user));
    }

    public Optional<User> login(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("username or password is null");
        }

        return userList
                .stream()
                .filter(user -> user.getUsername().equals(username))
                .filter(user -> user.getPassword().equals(password))
                .findFirst();
    }

    public Map<Integer, User> getAllConvertedById() {
        return userList
                .stream()
                .collect(toMap(User::getId, Function.identity()));
    }
}
