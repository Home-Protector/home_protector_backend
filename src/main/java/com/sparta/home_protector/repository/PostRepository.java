package com.sparta.home_protector.repository;

import com.sparta.home_protector.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

}