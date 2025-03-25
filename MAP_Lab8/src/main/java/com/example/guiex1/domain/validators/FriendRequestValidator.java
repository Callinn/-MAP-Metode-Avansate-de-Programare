package com.example.guiex1.domain.validators;

import com.example.guiex1.domain.FriendRequest;

public class FriendRequestValidator implements Validator<FriendRequest> {

    @Override
    public void validate(FriendRequest entity) throws ValidationException {
        StringBuilder errors = new StringBuilder();

        if (entity.getSenderId() == null || entity.getSenderId() <= 0) {
            errors.append("ID-ul expeditorului trebuie să fie un număr pozitiv.\n");
        }

        if (entity.getReceiverId() == null || entity.getReceiverId() <= 0) {
            errors.append("ID-ul destinatarului trebuie să fie un număr pozitiv.\n");
        }

        if (entity.getSenderId() != null && entity.getReceiverId() != null && entity.getSenderId().equals(entity.getReceiverId())) {
            errors.append("Expeditorul și destinatarul nu pot fi aceeași persoană.\n");
        }

        if (entity.getDateSent() == null) {
            errors.append("Data trimiterii cererii de prietenie nu poate fi null.\n");
        }

        if (errors.length() > 0) {
            throw new ValidationException(errors.toString());
        }
    }
}
