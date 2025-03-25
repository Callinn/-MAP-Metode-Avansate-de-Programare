package com.example.guiex1.service;

import com.example.guiex1.domain.Message;
import com.example.guiex1.domain.User;
import com.example.guiex1.repository.Repository;
import com.example.guiex1.repository.database.MessageDBRepository;
import com.example.guiex1.util.events.ChangeEventType;
import com.example.guiex1.util.events.UtilizatorEntityChangeEvent;
import com.example.guiex1.util.observer.Observer;
import com.example.guiex1.util.observer.Observable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MessageService implements Observable<UtilizatorEntityChangeEvent>{
    private MessageDBRepository messageDBRepository;
    private List<Observer<UtilizatorEntityChangeEvent>> observers=new ArrayList<>();

    public MessageService(MessageDBRepository messageDBRepository){
        this.messageDBRepository =messageDBRepository;
    }
    public void sendMessage(Message message) {
        messageDBRepository.save(message); // Salvează mesajul în baza de date
        UtilizatorEntityChangeEvent event = new UtilizatorEntityChangeEvent(ChangeEventType.ADD, message);
        notifyObservers(event); // Notifică observatorii de o schimbare
    }
    public List<Message> getMessagesBetween(Long id1, Long id2) {
        return StreamSupport.stream(messageDBRepository.findAll().spliterator(), false)
                .filter(message ->
                        (message.getFrom().equals(id1) && message.getTo().equals(id2)) ||
                                (message.getFrom().equals(id2) && message.getTo().equals(id1)))
                .sorted((m1, m2) -> m1.getData().compareTo(m2.getData())) // Sortare după timp
                .collect(Collectors.toList());
    }

    @Override
    public void addObserver(Observer<UtilizatorEntityChangeEvent> e) {
        observers.add(e);

    }
    @Override
    public void removeObserver(Observer<UtilizatorEntityChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(UtilizatorEntityChangeEvent t) {

        observers.stream().forEach(x->x.update(t));
    }
    public Iterable<Message> getAll(){return messageDBRepository.findAll();}


}
