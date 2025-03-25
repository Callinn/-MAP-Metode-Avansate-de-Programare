package com.example.map_lab6.src.domain.validators;

import com.example.map_lab6.src.domain.Friendship;
import com.example.map_lab6.src.domain.User;
import com.example.map_lab6.src.repository.database.FriendshipDBRepository;
import com.example.map_lab6.src.repository.database.UserDBRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public class FriendshipValidator implements Validator<Friendship> {

    private UserDBRepository repo;

    public FriendshipValidator(UserDBRepository repo) {
        this.repo = repo;
    }

    @Override
    public void validate(Friendship entity) throws ValidationException {

        Optional<User> u1 = repo.findOne(entity.getIdUser1());
        Optional<User> u2 = repo.findOne(entity.getIdUser2());

        if (entity.getIdUser1() == null || entity.getIdUser2() == null)
            throw new ValidationException("The id can't be null! ");
        if (u1.isEmpty() || u2.isEmpty())
            throw new ValidationException("The id doesn't exist! ");
        if (entity.getDate().isAfter(LocalDateTime.now()))
            throw new ValidationException("The friendship date can't be in the future!");
        if (entity.getDate() == null)
            throw new ValidationException("The friendship date can't be null!");

    }
}