package com.example.map_lab6.src.util.observer;
import com.example.map_lab6.src.util.events.Event;

public interface Observer<E extends Event> {
    void update(E e);
}