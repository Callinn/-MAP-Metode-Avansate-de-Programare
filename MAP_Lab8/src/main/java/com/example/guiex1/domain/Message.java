package com.example.guiex1.domain;

import java.time.LocalDateTime;

public class Message extends Entity<Long> {

    private Long from;
    private Long to;
    private String message;
    private LocalDateTime data;

    public Message(Long from, Long to, String message, LocalDateTime data) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.data = data;
    }

    public Long getFrom() {
        return from;
    }

    public Long getTo() {
        return to;
    }

    public void setTo(Long to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getData() {
        return data;
    }
}
