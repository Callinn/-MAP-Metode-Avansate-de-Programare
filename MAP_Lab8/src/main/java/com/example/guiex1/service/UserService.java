package com.example.guiex1.service;

import com.example.guiex1.domain.FriendRequest;
import com.example.guiex1.domain.User;
import com.example.guiex1.domain.UserCredentials;
import com.example.guiex1.domain.validators.ValidationException;
import com.example.guiex1.repository.Repository;
import com.example.guiex1.repository.database.FriendRequestDBRepository;
import com.example.guiex1.repository.database.UserCredentialsDBRepository;
import com.example.guiex1.repository.database.UserDBRepository;
import com.example.guiex1.util.events.ChangeEventType;
import com.example.guiex1.util.events.UtilizatorEntityChangeEvent;
import com.example.guiex1.util.observer.Observable;
import com.example.guiex1.util.observer.Observer;
import com.example.guiex1.domain.Friendship;
import com.example.guiex1.repository.database.FriendshipDBRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserService implements Observable<UtilizatorEntityChangeEvent> {
    private UserDBRepository repo;
    private FriendshipDBRepository repositoryFriendship;
    private FriendRequestDBRepository friendRequestDBRepository;
    private UserCredentialsDBRepository credentialsRepository; // Adăugat pentru gestionarea credențialelor
    private List<Observer<UtilizatorEntityChangeEvent>> observers=new ArrayList<>();


    public UserService(UserDBRepository repo, FriendshipDBRepository repositoryFriendship, FriendRequestDBRepository friendRequestDBRepository,UserCredentialsDBRepository credentialsRepository) {
        this.repo = repo;
        this.repositoryFriendship = repositoryFriendship;
        this.friendRequestDBRepository = friendRequestDBRepository;
        this.credentialsRepository = credentialsRepository;
    }



    public Optional<User> findUserById(Long userId) {
        try {
            Optional<User> user = repo.findOne(userId); // Aici presupunem că repo este un `UserRepository`
            if (user.isPresent()) {
                return user;
            } else {
                return Optional.empty(); // Utilizatorul nu este găsit
            }
        } catch (Exception e) {
            // Log error or show appropriate message
            System.err.println("Failed to find user with ID " + userId + ": " + e.getMessage());
            return Optional.empty(); // În cazul unei erori, returnăm un Optional gol
        }
    }

    public Optional<User> findUserByEmailAndPassword(String email, String password) {
        try {
            for (UserCredentials credentials : credentialsRepository.findAll()) {
                if (credentials.getEmail().equals(email) && credentials.getPassword().equals(password)) {
                    return findUserById(Long.valueOf(credentials.getUserId())); // ID-ul utilizatorului
                }
            }
            return Optional.empty(); // Credențiale invalide
        } catch (Exception e) {
            throw new RuntimeException("Error in findUserByEmailAndPassword: " + e.getMessage(), e);
        }
    }




    public User addUser(User user) {
        if(repo.save(user).isEmpty()){
            UtilizatorEntityChangeEvent event = new UtilizatorEntityChangeEvent(ChangeEventType.ADD, user);
            notifyObservers(event);
            return null;
        }
        return user;
    }

    public UserCredentials addCredentials(UserCredentials userCredentials) {
        if(credentialsRepository.save(userCredentials).isEmpty()){
            UtilizatorEntityChangeEvent event = new UtilizatorEntityChangeEvent(ChangeEventType.ADD, userCredentials);
            notifyObservers(event);
            return null;
        }
        return userCredentials;
    }

    public Iterable<User> getAll(){return repo.findAll();}

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

    public User removeUser(Long id) {
        try {
            User user = repo.findOne(id).orElseThrow(() -> new ValidationException("User doesn't exist!"));
            List<Long> friendshipsToDelete = new ArrayList<>();
            for (Friendship friendship : repositoryFriendship.findAll()) {
                if (friendship.getIdUser1().equals(id) || friendship.getIdUser2().equals(id)) {
                    friendshipsToDelete.add(friendship.getId());
                }
            }
            for (Long friendshipId : friendshipsToDelete) {
                repositoryFriendship.delete(friendshipId);
            }

            List<Long> requestToDelete = new ArrayList<>();
            for (FriendRequest friendRequest : friendRequestDBRepository.findAll()) {
                if (friendRequest.getSenderId().equals(id) || friendRequest.getReceiverId().equals(id)) {
                    requestToDelete.add(friendRequest.getId());
                }
            }
            for (Long requestId : requestToDelete) {
                friendRequestDBRepository.delete(requestId);
            }

            // Șterge utilizatorul
            User deletedUser = repo.delete(id).orElseThrow(() -> new ValidationException("User doesn't exist!"));
            return deletedUser;

        } catch (IllegalArgumentException e) {
            System.out.println("Invalid user: " + e.getMessage());
        } catch (ValidationException v) {
            System.out.println("Validation error: " + v.getMessage());
        }
        return null;
    }


}
