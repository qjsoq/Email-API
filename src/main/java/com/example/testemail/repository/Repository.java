package com.example.testemail.repository;

public interface Repository<T, K>{
    void create(T object);
    T update(T object);
    void delete(T object);
    T getById(K id);
}
