package com.example.guiex1.domain.validators;

public interface Validator<T> {
    void validate(T entity) throws ValidationException;

}
