package com.tuananhdo.repository;

import com.tuananhdo.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = "select c.* from comments c inner join posts p on c.post_id = p.id and p.created_by =:userId", nativeQuery = true)
    List<Comment> findCommentsByPost(Long userId);

    @Query(value = "select count(*) from comments where post_id =:postId", nativeQuery = true)
    Long countCommentsByPostId(Long postId);

    @Query("SELECT c FROM Comment c WHERE c.id =:commentId")
    void getCommentById(Long commentId);

    List<Comment> findByPostId(Long postId);

//    @Query("UPDATE Comment c SET c.votes = (SELECT SUM (cl.votes) FROM CommentLike cl WHERE cl.comment.id = ?1) WHERE c.id = ?1")
//    @Modifying
//    @Transactional
//    void updateVoteCountByComment(Long commentId);

    @Query("SELECT c.votes FROM Comment c WHERE c.id = ?1")
    @Transactional
    Long getVoteCountByComment(Long commentId);

}
