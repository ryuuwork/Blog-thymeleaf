package com.tuananhdo.repository;

import com.tuananhdo.entity.Post;
import com.tuananhdo.entity.User;
import com.tuananhdo.paging.SearchRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends SearchRepository<Post, Long> {
    Optional<Post> findByUrl(String url);

    @Query("select p from Post p where " +
            "p.title like concat('%',:search,'%') or " +
            "p.shortDescription like concat('%',:search,'%') ")
    List<Post> searchPosts(String search);
    @Query("SELECT p FROM Post p WHERE p.createdBy = ?1")
    Page<Post> findAllByUser(User user, Pageable pageable);
    @Query("SELECT p FROM Post p WHERE p.createdBy = ?1 " +
            "AND CONCAT(LOWER(p.id), '',LOWER(p.title), '',LOWER(p.shortDescription)) LIKE %?2%")
    Page<Post> findAllKeyWordByUser(User user, Pageable pageable, String keyword);
    List<Post> findAll();
    @Query("SELECT p FROM Post p WHERE CONCAT(LOWER(p.id), '',LOWER(p.title), '',LOWER(p.shortDescription)) LIKE %?1%")
    Page<Post> findAllByKeyWord(Pageable pageable, String keyword);
}
