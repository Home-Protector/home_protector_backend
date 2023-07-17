package com.sparta.home_protector.repository;

import com.sparta.home_protector.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository  extends JpaRepository<Comment,Long> {
}