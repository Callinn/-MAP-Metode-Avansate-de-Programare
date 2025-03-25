package com.example.guiex1.service;

import com.example.guiex1.domain.FriendRequest;
import com.example.guiex1.domain.Friendship;
import com.example.guiex1.repository.database.FriendRequestDBRepository;
import com.example.guiex1.repository.database.FriendshipDBRepository;
import com.example.guiex1.repository.database.UserDBRepository;
import com.example.guiex1.util.events.ChangeEventType;
import com.example.guiex1.util.events.UtilizatorEntityChangeEvent;
import com.example.guiex1.util.observer.Observer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class FriendshipService {
    private FriendshipDBRepository repositoryFriendship;
    private FriendRequestDBRepository repositoryRequest;
    private UserDBRepository repositoryUser;
    private List<Observer> observers = new ArrayList<>();



    public FriendshipService(FriendshipDBRepository repositoryFriendship, FriendRequestDBRepository repositoryRequest, UserDBRepository repositoryUser) {
        this.repositoryFriendship = repositoryFriendship;
        this.repositoryRequest = repositoryRequest;
        this.repositoryUser = repositoryUser;
    }


    public Iterable<Friendship> getFriendshipsForUser(Long userId) {

        Stream<Friendship> friendshipStream = StreamSupport.stream(repositoryFriendship.findAll().spliterator(), false);

        return friendshipStream
                .filter(friendship -> friendship.getIdUser1().equals(userId) || friendship.getIdUser2().equals(userId))
                .toList();
    }

    public List<FriendRequest> getFriendRequests(Long userId) {
        return StreamSupport.stream(repositoryRequest.findAll().spliterator(), false)
                .filter(request -> request.getReceiverId().equals(userId))
                .toList();
    }

    public Friendship acceptFriendRequest(FriendRequest request) {
        try {
            // Găsește cererea de prietenie
            request = repositoryRequest.findOne(request.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Cererea de prietenie nu există!"));

            // Creează o prietenie între utilizatori
            Friendship friendship = new Friendship(request.getSenderId(), request.getReceiverId(), LocalDate.now());
            repositoryFriendship.save(friendship);
            System.out.println("ID cerere: " + request.getId());

            // Șterge cererea de prietenie după acceptare
            repositoryRequest.delete(request.getId());

            // Trimite un eveniment de modificare a utilizatorului
            UtilizatorEntityChangeEvent event = new UtilizatorEntityChangeEvent(ChangeEventType.ADD, friendship);
            notifyObservers(event);

            return friendship;
        } catch (Exception e) {
            // Gestionați excepțiile, poate fi o excepție customă sau o gestionare generală
           new IllegalArgumentException(e.getMessage());
            return null;
        }
    }


    public void denyFriendRequest(Long requestId) {
        // Șterge cererea de prietenie
        if (repositoryRequest.findOne(requestId).isEmpty()) {
            throw new IllegalArgumentException("Cererea de prietenie nu există!");
        }
        repositoryRequest.delete(requestId);
    }

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void notifyObservers(UtilizatorEntityChangeEvent event) {
        for (Observer observer : observers) {
            observer.update(event);
        }
    }

    public void sendFriendRequest(Long senderId, Long receiverId) {
        // Verifică dacă cererea deja există sau utilizatorii sunt deja prieteni
        boolean alreadyFriends = areFriends(senderId, receiverId);

        if (alreadyFriends) {
            throw new IllegalStateException("Utilizatorii sunt deja prieteni.");
        }

        boolean requestExists = hasPendingRequest(senderId, receiverId);

        if (requestExists) {
            throw new IllegalStateException("Cererea a fost deja trimisă.");
        }

        // Verifică dacă utilizatorul există în baza de date
        if (repositoryUser.findOne(senderId).isEmpty() || repositoryUser.findOne(receiverId).isEmpty()) {
            throw new IllegalArgumentException("Utilizatorul nu există!");
        }

        // Creează o nouă cerere de prietenie
        FriendRequest request = new FriendRequest( senderId, receiverId, LocalDate.now());
        repositoryRequest.save(request);
    }


    public void removeFriendship(Long userId1, Long userId2) {
        // Căutăm prietenia dintre userId1 și userId2
        Friendship friendship = StreamSupport.stream(repositoryFriendship.findAll().spliterator(), false)
                .filter(f -> (f.getIdUser1().equals(userId1) && f.getIdUser2().equals(userId2)) ||
                        (f.getIdUser1().equals(userId2) && f.getIdUser2().equals(userId1)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nu există prietenie între acești utilizatori."));

        // Ștergem prietenia găsită
        repositoryFriendship.delete(friendship.getId());
    }

    public boolean areFriends(Long userId1, Long userId2) {
        return StreamSupport.stream(repositoryFriendship.findAll().spliterator(), false)
                .anyMatch(friendship ->
                        (friendship.getIdUser1().equals(userId1) && friendship.getIdUser2().equals(userId2)) ||
                                (friendship.getIdUser1().equals(userId2) && friendship.getIdUser2().equals(userId1)));
    }

    public boolean hasPendingRequest(Long senderId, Long receiverId) {
        return StreamSupport.stream(repositoryRequest.findAll().spliterator(), false)
                .anyMatch(request -> request.getSenderId().equals(senderId) && request.getReceiverId().equals(receiverId));
    }


}
