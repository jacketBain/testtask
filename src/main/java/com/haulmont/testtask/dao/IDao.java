package com.haulmont.testtask.dao;

import java.util.*;

public interface IDao<T,E> {
    T findById(E e);
    void save(T t);
    void update(T t);
    void delete(T t);
    List<T> findAll();
}
