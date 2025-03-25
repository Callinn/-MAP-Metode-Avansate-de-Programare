package com.example.guiex1.util.observer;


import com.example.guiex1.util.events.Event;

public interface Observer<E extends Event> {
    void update(E e);
}