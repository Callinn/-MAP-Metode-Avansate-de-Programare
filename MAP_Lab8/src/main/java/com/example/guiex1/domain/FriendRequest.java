package com.example.guiex1.domain;

import java.time.LocalDate;

public class FriendRequest extends Entity<Long> {
    private Long senderId;
    private Long receiverId;
    private LocalDate dateSent;

    public FriendRequest(Long senderId, Long receiverId, LocalDate dateSent) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.dateSent = dateSent;
    }

    public Long getSenderId() {
        return senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public LocalDate getDateSent() {
        return dateSent;
    }
}
