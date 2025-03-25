package com.example.map_lab6.src.util.observer;


import com.example.map_lab6.src.util.events.Event;

public interface Observable<E extends Event> {
    void addObserver(Observer<E> e);
    void removeObserver(Observer<E> e);
    void notifyObservers(E t);
}
