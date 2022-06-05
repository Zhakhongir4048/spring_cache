package com.example.spring_cache.service;

import com.example.spring_cache.entity.User;

import java.util.List;

public interface UserService {

    User create(User user);

    User get(Integer id);

    User create(String name, String email);

    List<User> getAll();

    User createOrReturnCached(User user);

    User createAndRefreshCache(User user);

    void delete(Integer id);

    void deleteAndEvict(Integer id);

    void cacheExample(User user);

}