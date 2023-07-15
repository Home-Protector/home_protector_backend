package com.sparta.home_protector.repository;

import com.sparta.home_protector.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
