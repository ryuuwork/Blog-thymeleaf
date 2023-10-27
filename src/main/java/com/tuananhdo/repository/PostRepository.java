package com.tuananhdo.repository;

import com.tuananhdo.entity.Post;
import com.tuananhdo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findByUrl(String url);

    @Query("select p from Post p where " +
            "p.title like concat('%',:search,'%') or " +
            "p.shortDescription like concat('%',:search,'%') ")
    List<Post> searchPosts(String search);

    @Query("SELECT p FROM Post p WHERE p.createdBy = :user")
    Page<Post> findPostByUser(User user, Pageable pageable);
}
