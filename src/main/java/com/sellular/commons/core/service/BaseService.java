package com.sellular.commons.core.service;

import com.sellular.commons.jpa.dao.BaseDao;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Getter
@Slf4j
public abstract class BaseService<T> {

    protected final BaseDao<T> baseDao;

    public void create(final T t) {
        baseDao.create(t);
    }

    public T findById(final Long id) {
        return baseDao.findById(id);
    }

    public T update(T t) {
        return baseDao.update(t);
    }

}
