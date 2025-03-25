package com.example.guiex1.domain.validators;

import com.example.guiex1.domain.Friendship;
import com.example.guiex1.domain.User;
import com.example.guiex1.repository.Repository;
import com.example.guiex1.repository.database.UserDBRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public class FriendshipValidator implements Validator<Friendship> {

    private Repository<Long, User> repo;

    public FriendshipValidator(Repository<Long, User> repo) {
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
        if (entity.getDate().isAfter(LocalDate.now()))
            throw new ValidationException("The friendship date can't be in the future!");
        if (entity.getDate() == null)
            throw new ValidationException("The friendship date can't be null!");

    }
}