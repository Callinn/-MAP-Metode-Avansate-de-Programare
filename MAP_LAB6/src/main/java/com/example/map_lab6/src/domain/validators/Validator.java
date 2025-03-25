package com.example.map_lab6.src.domain.validators;

public interface Validator<T> {
    void validate(T entity) throws ValidationException;

}
