package com.example.spring_cache.service;

import com.example.spring_cache.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class UserServiceImplTest {

    Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserService userService;

    @Test
    void get() {
        User user1 = userService.create(new User("Вася", "vasya@mail.ru"));
        User user2 = userService.create(new User("Коля", "kolya@mail.ru"));

        getAndPrint(user1.getId());
        getAndPrint(user2.getId());
        getAndPrint(user1.getId());
        getAndPrint(user2.getId());
    }

    @Test
    void create() {
        createAndPrint("Ivan", "ivan@mail.ru");
        createAndPrint("Ivan", "ivan1122@mail.ru");
        createAndPrint("Sergey", "ivan@mail.ru");
        log.info("all entries are below:");
        userService.getAll().forEach(u -> log.info("{}", u.toString()));
    }

    @Test
    public void createAndRefresh() {
        User user1 = userService.createOrReturnCached(new User("Vasya", "vasya@mail.ru"));
        log.info("created user1: {}", user1);

        User user2 = userService.createOrReturnCached(new User("Vasya", "misha@mail.ru"));
        log.info("created user2: {}", user2);

        User user3 = userService.createAndRefreshCache(new User("Vasya", "kolya@mail.ru"));
        log.info("created user3: {}", user3);

        User user4 = userService.createOrReturnCached(new User("Vasya", "petya@mail.ru"));
        log.info("created user4: {}", user4);
    }

    @Test
    public void delete() {
        User user1 = userService.create(new User("Vasya", "vasya@mail.ru"));
        log.info("{}", userService.get(user1.getId()));

        User user2 = userService.create(new User("Vasya", "vasya@mail.ru"));
        log.info("{}", userService.get(user2.getId()));

        userService.delete(user1.getId());
        userService.deleteAndEvict(user2.getId());

        log.info("{}", userService.get(user1.getId()));
        EntityNotFoundException thrown = Assertions.assertThrows(EntityNotFoundException.class,
                () -> userService.get(user2.getId()));

        Assertions.assertEquals("User not found by id " + user2.getId(), thrown.getMessage());
    }

    @Test
    public void checkSettings() throws InterruptedException {
        User user1 = userService.createOrReturnCached(new User("Vasya", "vasya@mail.ru"));
        log.info("{}", userService.get(user1.getId()));

        User user2 = userService.createOrReturnCached(new User("Vasya", "vasya@mail.ru"));
        log.info("{}", userService.get(user2.getId()));

        Thread.sleep(1000L);
        User user3 = userService.createOrReturnCached(new User("Vasya", "vasya@mail.ru"));
        log.info("{}", userService.get(user3.getId()));
    }

    private void getAndPrint(Integer id) {
        log.info("user found: {}", userService.get(id));
    }

    private void createAndPrint(String name, String email) {
        log.info("created user: {}", userService.create(name, email));
    }

}