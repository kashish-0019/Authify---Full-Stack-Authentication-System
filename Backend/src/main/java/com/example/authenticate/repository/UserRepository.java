package com.example.authenticate.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.authenticate.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long>
{
     Optional<UserEntity> findByEmailIgnoreCase(String email);
     boolean existsByEmailIgnoreCase(String email);
   
}
