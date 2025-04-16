package com.sellular.commons.jpa.querybuilder;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.Getter;

@Getter(AccessLevel.PACKAGE)
public class JPACountQueryBuilder<E> extends JPABaseQueryBuilder<E, Long, JPACountQueryBuilder<E>> {

    private JPACountQueryBuilder(final EntityManager entityManager, final Class<E> entityClass) {
        super(entityManager, entityClass, Long.class);
    }

    @Override
    protected JPACountQueryBuilder<E> self() {
        return this;
    }

    public static <T> JPACountQueryBuilder<T> init(final EntityManager entityManager, final Class<T> entityClass) {
        return new JPACountQueryBuilder<>(entityManager, entityClass);
    }

    public long count() {
        getCriteriaQuery().select(getCriteriaBuilder().countDistinct(getRoot()));
        getCriteriaQuery().where(getPredicates().toArray(new Predicate[] {}));
        final TypedQuery<Long> typedQuery = getEntityManager().createQuery(getCriteriaQuery());
        return typedQuery.getSingleResult();
    }

}
