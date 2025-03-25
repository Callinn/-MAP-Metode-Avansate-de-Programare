package com.example.guiex1.domain.validators;

import com.example.guiex1.domain.Message;

import java.time.LocalDateTime;

public class MessageValidator implements Validator<Message>{
    @Override
    public void validate(Message entity) throws ValidationException{
        String errorMessage = "";
        if(entity.getFrom() == 0){
            errorMessage+= "Id ul sendarul-ui nu poate fi null";
        }
        if(entity.getTo() == 0){
            errorMessage+= "Id ul receverul-ui nu poate fi null";
        }
        if(entity.getMessage().isEmpty()){
            errorMessage+= "Mesajul nu poate fi null";
        }
        if(entity.getData().isAfter(LocalDateTime.now())){
            errorMessage+="data nu poate fi in viitor";
        }

        System.out.println(errorMessage);
        if (!errorMessage.equals("")) {
            throw new ValidationException(errorMessage);
        }
    }
}
