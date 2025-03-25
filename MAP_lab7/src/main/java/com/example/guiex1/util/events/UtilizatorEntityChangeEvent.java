package com.example.guiex1.util.events;
import com.example.guiex1.domain.*;

public class UtilizatorEntityChangeEvent implements Event {
    private ChangeEventType type;
    private Object entity;

    public UtilizatorEntityChangeEvent(ChangeEventType type, User user) {
        this.type = type;
        this.entity  = user;
    }
    public UtilizatorEntityChangeEvent(ChangeEventType type, UserCredentials userCredentials) {
        this.type = type;
        this.entity  = userCredentials;
    }
    public UtilizatorEntityChangeEvent(ChangeEventType type, FriendRequest request) {
        this.type = type;
        this.entity = request;
    }

    public UtilizatorEntityChangeEvent(ChangeEventType type, Friendship friendship) {
        this.type = type;
        this.entity = friendship;
    }
    public UtilizatorEntityChangeEvent(ChangeEventType type, Message message) {
        this.type = type;
        this.entity = message;
    }


    public ChangeEventType getType() {
        return type;
    }

    public Object getEntity() {
        return entity;
    }
}