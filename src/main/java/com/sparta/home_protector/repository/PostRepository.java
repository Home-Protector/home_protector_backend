package com.sparta.home_protector.repository;

import com.sparta.home_protector.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findById(@Param("postId") Long postId);
}
