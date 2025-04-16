package com.sellular.commons.jpa.dao;

public interface BaseDao<T> {

    void create(T t);

    T findById(Long id);

    T update(final T t);

}
