package com.example.guiex1.domain.validators;
import com.example.guiex1.domain.UserCredentials;

public class CredentialsValidator implements Validator<UserCredentials>{

    @Override
    public void validate(UserCredentials entity) throws ValidationException {
        String errorMessage = "";

        if (entity.getEmail().isEmpty()) {
            errorMessage += "Email can't be null! ";
        }else if (!entity.getEmail().contains("@")) {
            errorMessage += "Email must contain '@'! ";
        }

        if (entity.getPassword().isEmpty()) {
            errorMessage += "Password can't be null! ";
        }
        if(entity.getUserId()==null){
            errorMessage +="Id of user can't be null";
        }

        System.out.println(errorMessage);
        if (!errorMessage.equals("")) {
            throw new ValidationException(errorMessage);
        }
    }
}

