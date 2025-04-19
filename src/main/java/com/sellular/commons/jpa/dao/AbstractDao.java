package com.sellular.commons.jpa.dao;

import com.google.inject.Provider;
import com.sellular.commons.core.domain.PaginatedResult;
import com.sellular.commons.jpa.querybuilder.JPACountQueryBuilder;
import com.sellular.commons.jpa.querybuilder.JPAQueryBuilder;
import jakarta.persistence.EntityManager;
import com.google.inject.persist.Transactional;
import jakarta.persistence.NoResultException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    private JPAQueryBuilder<T> queryBuilder() {
        return JPAQueryBuilder.init(getEntityManager(), entityClass).equal("deleted", Boolean.FALSE);
    }

    private JPACountQueryBuilder<T> countQueryBuilder() {
        return JPACountQueryBuilder.init(getEntityManager(), entityClass).equal("deleted", Boolean.FALSE);
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

    private PaginatedResult<T> getPaginatedResultInternal(final Integer perPage,
                                                          final Integer pageNo,
                                                          final String orderByField,
                                                          final Boolean orderByAsc) {
        try {
            final int safePerPage = (perPage == null || perPage <= 0) ? 10 : perPage;
            final int safePageNo = (pageNo == null || pageNo <= 0) ? 1 : pageNo;
            final boolean isAscending = Optional.ofNullable(orderByAsc).orElse(true);

            JPAQueryBuilder<T> builder = queryBuilder()
                    .setPageNo(safePageNo)
                    .setPerPage(safePerPage);

            if (orderByField != null && !orderByField.isBlank()) {
                builder.orderBy(orderByField, isAscending);
            }

            long totalCount = countQueryBuilder().count();
            List<T> results = builder.list();
            boolean hasNextPage = ((long) safePageNo * safePerPage) < totalCount;

            return new PaginatedResult<>(results, hasNextPage, totalCount);

        } catch (final Exception e) {
            log.error("Error fetching paginated result for {} - pageNo: {}, perPage: {}, orderByField: {}, orderByAsc: {}",
                    entityClass.getSimpleName(), pageNo, perPage, orderByField, orderByAsc, e);
            throw e;
        }
    }

    public PaginatedResult<T> getPaginatedResult(final Integer perPage, final Integer pageNo) {
        return getPaginatedResultInternal(perPage, pageNo, null, null);
    }

    public PaginatedResult<T> getPaginatedResultWithOrdering(final Integer perPage,
                                                             final Integer pageNo,
                                                             final String orderByField,
                                                             final Boolean orderByAsc) {
        return getPaginatedResultInternal(perPage, pageNo, orderByField, orderByAsc);
    }

    public Optional<T> findByExternalId(final String externalId) {
        try {
            final JPAQueryBuilder<T> queryBuilder = queryBuilder();
            queryBuilder.equal("externalId", externalId);
            return queryBuilder.first();
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

}
