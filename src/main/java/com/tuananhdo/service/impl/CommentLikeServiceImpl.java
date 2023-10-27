//package com.tuananhdo.service.impl;
//
//import com.tuananhdo.entity.Comment;
//import com.tuananhdo.entity.User;
//import com.tuananhdo.repository.CommentLikeRepository;
//import com.tuananhdo.repository.CommentRepository;
//import com.tuananhdo.repository.UserRepository;
//import com.tuananhdo.utils.VoteResult;
//import com.tuananhdo.utils.VoteType;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Service;
//
//import javax.transaction.Transactional;
//
//@Service
//public class CommentLikeServiceImpl implements CommentLikeService {
//
//    private final CommentRepository commentRepository;
//    private final CommentLikeRepository commentLikeRepository;
//    private final UserRepository userRepository;
//
//    public CommentLikeServiceImpl(CommentRepository commentRepository, CommentLikeRepository commentLikeRepository, UserRepository userRepository) {
//        this.commentRepository = commentRepository;
//        this.commentLikeRepository = commentLikeRepository;
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    public User getCurrentUserByCommentLike() {
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        if (principal instanceof UserDetails) {
//            UserDetails userDetails = (UserDetails) principal;
//            return userRepository.findByEmail(userDetails.getUsername());
//        }
//        return null;
//    }
//
//    @Override
//    public void getCommentById(Long id) {
//        commentRepository.getCommentById(id);
//    }
//
//    @Override
//    public VoteResult undoVote(CommentLike vote, Long commentId, VoteType voteType) {
//        commentLikeRepository.delete(vote);
//        commentRepository.updateVoteCountByComment(commentId);
//        Long voteCount = commentRepository.getVoteCountByComment(commentId);
//        return VoteResult.success("You have unvoted " + voteType + " that review.", +voteCount);
//    }
//
//    @Override
//    @Transactional
//    public VoteResult doVote(Long commentId, User user, VoteType voteType) {
//        Comment comment = commentRepository.findById(commentId).orElse(null);
//        if (comment == null) {
//            return VoteResult.fail("The commentId " + commentId + "no loger exists");
//        }
//        CommentLike vote = commentLikeRepository.findByCommentAndUser(commentId, user.getId());
//        if (vote != null) {
//            if (vote.isVoteUp() && voteType.equals(VoteType.UP)
//                    || vote.isVoteDown() && voteType.equals(VoteType.DOWN)) {
//                return undoVote(vote, commentId, voteType);
//            } else if (vote.isVoteUp() && voteType.equals(VoteType.DOWN)) {
//                vote.voteDown();
//            } else if (vote.isVoteDown() && voteType.equals(VoteType.UP)) {
//                vote.voteUp();
//            }
//        } else {
//            CommentLike voteComment = new CommentLike();
//            voteComment.setComment(comment);
//            voteComment.setUser(user);
//            vote = voteComment;
//            if (voteType.equals(VoteType.UP)) {
//                vote.voteUp();
//            } else {
//                vote.voteDown();
//            }
//        }
//        commentLikeRepository.save(vote);
//        commentRepository.updateVoteCountByComment(commentId);
//        Long voteCount = commentRepository.getVoteCountByComment(commentId);
//        return VoteResult.success("You have successfully voted " + voteType + " that review", voteCount);
//    }
//
//
//}