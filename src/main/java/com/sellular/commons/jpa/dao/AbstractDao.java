package com.sellular.commons.jpa.dao;

import com.google.inject.Provider;
import jakarta.persistence.EntityManager;
import com.google.inject.persist.Transactional;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Slf4j
public abstract class AbstractDao<T> implements BaseDao<T> {

    private final Provider<EntityManager> entityManager;

    @Getter
    private final Class<T> entityClass;

    public AbstractDao(final Provider<EntityManager> entityManager, final Class<T> entityClass) {
        this.entityManager = entityManager;
        this.entityClass = entityClass;
    }

    public EntityManager getEntityManager() {
        return this.entityManager.get();
    }

    public void create(final T entity) {
        persist(entity);
    }

    public T findById(final Long id) {
        try {
            return getEntityManager().find(entityClass, id);
        } catch (final Exception e) {
            log.info(e.toString());
            throw e;
        }
    }

    public T update(final T entity) {
        return merge(entity);
    }

    protected void persist(final T entity) {
        try {
            getEntityManager().persist(entity);
        } catch (final Exception e) {
            log.info(e.toString());
        }
    }

    protected T merge(final T entity) {
        try {
            return getEntityManager().merge(entity);
        } catch (final Exception e) {
            log.info(e.toString());
            throw e;
        }
    }

}
