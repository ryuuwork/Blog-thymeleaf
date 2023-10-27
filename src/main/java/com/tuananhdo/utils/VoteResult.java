package com.tuananhdo.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VoteResult {
    private boolean successful;
    private String message;
    private Long voteCount;

    public static VoteResult fail(String message) {
        return new VoteResult(false, message, 0L);
    }

    public static VoteResult success(String message, Long voteCount) {
        return new VoteResult(true, message, voteCount);
    }
}
