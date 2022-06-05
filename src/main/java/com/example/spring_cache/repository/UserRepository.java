package com.example.spring_cache.repository;

import com.example.spring_cache.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}