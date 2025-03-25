//package com.example.guiex1.service;
//
//import com.example.guiex1.domain.Friendship;
//import com.example.guiex1.domain.User;
//import com.example.guiex1.domain.validators.ValidationException;
//import com.example.guiex1.repository.database.FriendshipDBRepository;
//import com.example.guiex1.repository.database.UserDBRepository;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Vector;
//import java.util.concurrent.atomic.AtomicReference;
//
//public class SocialNetwork {
//
//    private final UserDBRepository repositoryUser;
//    private final FriendshipDBRepository repositoryFriendship;
//
//    public SocialNetwork(UserDBRepository repositoryUser, FriendshipDBRepository repositoryFriendship) {
//        this.repositoryUser = repositoryUser;
//        this.repositoryFriendship = repositoryFriendship;
//    }
//
//    public Iterable<User> getUsers() {
//        return repositoryUser.findAll();
//    }
//
//    public User findUser(Long id) {
//        return repositoryUser.findOne(id).orElseThrow(() -> new ValidationException("No user"));
//    }
//
//
//    public Iterable<User> getAll(){
//        return repositoryUser.findAll();
//    }
//
//
//
//    public Long getNewUserId() {
//        AtomicReference<Long> id = new AtomicReference<>(0L);
//
//        repositoryUser.findAll().forEach(u ->{
//            id.set(u.getId());
//        });
//
//        id.getAndSet(id.get() + 1);
//        return id.get();
//    }
//
//    public User addUser(User user) {
//        user.setId(getNewUserId());
//        repositoryUser.save(user);
//        return user;
//    }
//
//    public Iterable<Friendship> getFriendships() {
///        return repositoryFriendship.findAll();
//    }
//
//    public User removeUser(Long id) {
//        try {
//            User u = repositoryUser.findOne(id).orElseThrow(() -> new ValidationException("User doesn't exist!"));
//            Vector<Long> toDelete = new Vector<>();
//            getFriendships().forEach(friendship -> {
//                if (friendship.getIdUser2().equals(id) || friendship.getIdUser1().equals(id)) {
//                    toDelete.add(friendship.getId());
//                }
//            });
//            toDelete.forEach(repositoryFriendship::delete);
//            User user = repositoryUser.delete(id).orElseThrow(() -> new ValidationException("User doesn't exist!"));
//
//
//            u.getFriends().forEach(friend->{
//                friend.removeFriend(u);
//            });
//
//            return user;
//        } catch (IllegalArgumentException e) {
//            System.out.println("Invalid user! ");
//        } catch (ValidationException v) {
///            System.out.println();
//        }
//        return null;
//    }
//
//
//
//
//    public Long getNewFriendshipId() {
//        AtomicReference<Long> id = new AtomicReference<>(0L);
//
//        repositoryFriendship.findAll().forEach(f->{
//            id.set(f.getId());
//        });
//
//        id.getAndSet(id.get() + 1);
//        return id.get();
//    }
//
//    public void addFriendship(Friendship friendship) {
//        User user1 = null;
//        User user2 = null;
//
//        try {
//            user1 = repositoryUser.findOne(friendship.getIdUser1())
//                    .orElseThrow(() -> new ValidationException("User doesn't exist!"));
//            user2 = repositoryUser.findOne(friendship.getIdUser2())
//                    .orElseThrow(() -> new ValidationException("User doesn't exist!"));
//        } catch (ValidationException v) {
//            System.out.println("Validation error: " + v.getMessage());
//            return;
//        }
//
//        if (getFriendships() != null) {
//            for (Friendship f : getFriendships()) {
//                if ((f.getIdUser1().equals(friendship.getIdUser1()) && f.getIdUser2().equals(friendship.getIdUser2())) ||
//                        (f.getIdUser1().equals(friendship.getIdUser2()) && f.getIdUser2().equals(friendship.getIdUser1()))) {
//                    throw new ValidationException("The friendship already exists!");
//                }
//            }
//        }
//
//        if (friendship.getIdUser1().equals(friendship.getIdUser2()))
//            throw new ValidationException("IDs can't be the same!");
//
//        // Setăm ID-ul și data curentă pentru noua prietenie
//        friendship.setId(getNewFriendshipId());
//        friendship = new Friendship(friendship.getIdUser1(), friendship.getIdUser2(), LocalDateTime.now());
///
//        repositoryFriendship.save(friendship);
//
//        user1.addFriend(user2);
//        user2.addFriend(user1);
//    }
//
//
//    public void removeFriendship(Long id1, Long id2) {
//        AtomicReference<Long> id = new AtomicReference<>(0L);
//        User user1 = null;
//        User user2 = null;
//        try {
//            user1 = repositoryUser.findOne(id1).orElseThrow(() -> new ValidationException("User with id " + id1 + " doesn't exist!"));
//            user2 = repositoryUser.findOne(id2).orElseThrow(() -> new ValidationException("User with id " + id2 + " doesn't exist!"));
//        } catch (ValidationException v) {
//            System.out.println();
//        }
//
//
//       repositoryFriendship.findAll().forEach(f->{
//           if ((f.getIdUser1().equals(id1) && f.getIdUser2().equals(id2)) || (f.getIdUser1().equals(id2) && f.getIdUser2().equals(id1)))
//               id.set(f.getId());
//       });
//
//
//        if (id.get() == 0L)
//            throw new IllegalArgumentException("The friendship doesn't exist!");
//
//
//        assert user1 != null;
//        user1.removeFriend(user2);
//        assert user2 != null;
//        user2.removeFriend(user1);
//    }
//
//    public Iterable<User> getFriendRequests(Long userId) {
//        User user = repositoryUser.findOne(userId).orElseThrow(() -> new ValidationException("User doesn't exist!"));
//        Vector<User> friendRequests = new Vector<>();
//
//        repositoryFriendship.findAll().forEach(friendship -> {
//            if (friendship.getIdUser2().equals(userId) && !user.getFriends().contains(findUser(friendship.getIdUser1()))) {
//                friendRequests.add(findUser(friendship.getIdUser1()));
//            }
//        });
//
//        return friendRequests;
//    }
//
//    public void acceptFriendRequest(Long userId, Long requesterId) {
//        User user = repositoryUser.findOne(userId).orElseThrow(() -> new ValidationException("User doesn't exist!"));
//        User requester = repositoryUser.findOne(requesterId).orElseThrow(() -> new ValidationException("Requester doesn't exist!"));
//
//        AtomicReference<Long> friendshipId = new AtomicReference<>(null);
//
//        repositoryFriendship.findAll().forEach(friendship -> {
//            if (friendship.getIdUser1().equals(requesterId) && friendship.getIdUser2().equals(userId)) {
//                friendshipId.set(friendship.getId());
//            }
//        });
//
//        if (friendshipId.get() == null) {
//            throw new ValidationException("Friendship request doesn't exist!");
//        }
//
//        user.addFriend(requester);
//        requester.addFriend(user);
//
//        repositoryFriendship.update(new Friendship(requesterId, userId, LocalDateTime.now())); // Updatează prietenia dacă e necesar
//    }
//
//    public void rejectFriendRequest(Long userId, Long requesterId) {
//        User user = repositoryUser.findOne(userId).orElseThrow(() -> new ValidationException("User doesn't exist!"));
//        User requester = repositoryUser.findOne(requesterId).orElseThrow(() -> new ValidationException("Requester doesn't exist!"));
//
//        AtomicReference<Long> friendshipId = new AtomicReference<>(null);
//
//        repositoryFriendship.findAll().forEach(friendship -> {
//            if (friendship.getIdUser1().equals(requesterId) && friendship.getIdUser2().equals(userId)) {
//                friendshipId.set(friendship.getId());
//            }
//        });
//
//        if (friendshipId.get() == null) {
//            throw new ValidationException("Friendship request doesn't exist!");
//        }
//
//        repositoryFriendship.delete(friendshipId.get()); // Șterge cererea de prietenie din baza de date
//    }
//
//    public List<User> loadFriendRequests(Long userId) {
//        List<User> friendRequests = new ArrayList<>();
//
//        // Parcurgem toate prieteniile
//        for (Friendship friendship : repositoryFriendship.findAll()) {
//            // Filtrăm prieteniile care au status "PENDING" și care au idUser2 ca userId-ul nostru
//            if (friendship.getIdUser2().equals(userId) && "PENDING".equals(friendship.getStatus())) {
//                // Găsim utilizatorul care este idUser1
//                User user = repositoryUser.findOne(friendship.getIdUser1()).orElse(null);
//               if (user != null) {
//                    friendRequests.add(user); // Adăugăm utilizatorul la lista de cereri de prietenie
//                }
//            }
//        }
//        return friendRequests;
//    }
//
//
//
//
//}