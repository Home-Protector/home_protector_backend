package com.sparta.home_protector.repository;

import com.sparta.home_protector.entity.Post;
import com.sparta.home_protector.entity.PostLike;
import com.sparta.home_protector.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByPostAndUser(Post post, User user);
}
