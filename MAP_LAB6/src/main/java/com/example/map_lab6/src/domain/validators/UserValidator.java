package com.example.map_lab6.src.domain.validators;

import com.example.map_lab6.src.domain.User;

public class UserValidator implements Validator<User> {

    @Override
    public void validate(User entity) throws ValidationException {
        String errorMessage = "";

        if (entity.getFirstName().isEmpty()) {
            errorMessage += "First name can't be null! ";
        }
        if (entity.getLastName().isEmpty()) {
            errorMessage += "Last name can't be null! ";
        }
        System.out.println(errorMessage);
        if (!errorMessage.equals("")) {
            throw new ValidationException(errorMessage);
        }
    }
}