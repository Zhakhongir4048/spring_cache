package com.example.spring_cache.service;

import com.example.spring_cache.entity.User;
import com.example.spring_cache.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(User user) {
        return userRepository.save(user);
    }

    @Override
    @Cacheable(value = "users", key = "#name")
    public User create(String name, String email) {
        log.info("creating user with parameters: {}, {}", name, email);
        return userRepository.save(new User(name, email));
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    @Cacheable(value = "users", key = "#user.name")
    public User createOrReturnCached(User user) {
        log.info("creating user: {}", user);
        return userRepository.save(user);
    }

    @Override
    @CachePut(value = "users", key = "#user.name")
    public User createAndRefreshCache(User user) {
        log.info("creating user: {}", user);
        return userRepository.save(user);
    }

    @Override
    @Cacheable("users")
    public User get(Integer id) {
        log.info("getting user by id: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found by id " + id));
    }

    @Override
    public void delete(Integer id) {
        log.info("deleting user by id: {}", id);
        userRepository.deleteById(id);
    }

    @Override
    @CacheEvict("users")
    public void deleteAndEvict(Integer id) {
        log.info("deleting user by id: {}", id);
        userRepository.deleteById(id);
    }

    @Caching(
            cacheable = {
                    @Cacheable("users"),
                    @Cacheable("contacts")
            },
            put = {
                    @CachePut("tables"),
                    @CachePut("chairs"),
                    @CachePut(value = "meals", key = "#user.email")
            },
            evict = {
                    @CacheEvict(value = "services", key = "#user.name")
            }
    )
    public void cacheExample(User user) {
    }

}