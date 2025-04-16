package com.sellular.commons.jpa.querybuilder;

import com.sellular.commons.jpa.constants.QueryTimeout;
import com.sellular.commons.jpa.constants.Queries;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.collections4.CollectionUtils;
import lombok.AccessLevel;
import lombok.Getter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Getter(AccessLevel.PACKAGE)
public class JPAQueryBuilder<E> extends JPABaseQueryBuilder<E, E, JPAQueryBuilder<E>> {

    private final List<Order> orders = new ArrayList<>();

    private Integer pageNo;

    private Integer perPage;

    private int timeout;

    private JPAQueryBuilder(final EntityManager entityManager, final Class<E> entityClass) {
        super(entityManager, entityClass, entityClass);
    }

    public static <T> JPAQueryBuilder<T> init(final EntityManager entityManager, final Class<T> entityClass) {
        return new JPAQueryBuilder<>(entityManager, entityClass);
    }

    @Override
    protected JPAQueryBuilder<E> self() {
        return this;
    }

    public List<E> list() {
        final TypedQuery<E> typedQuery = prepareSelectTypedQuery();
        setPagination(typedQuery);
        if ( timeout > 0 ) {
            return executeQueryWithTimeout(typedQuery, timeout);
        }
        final List<E> result = typedQuery.getResultList();
        if (CollectionUtils.isEmpty(result)) {
            return Collections.emptyList();
        }
        return result;
    }

    private List<E> executeQueryWithTimeout(TypedQuery<E> typedQuery, long timeout) {
        int prevtime = getMaxTime();
        setExecutionTimeout(timeout);
        try {
            final List<E> result = typedQuery.getResultList();
            if (CollectionUtils.isEmpty(result)) {
                return Collections.emptyList();
            }
            return result;
        }finally {
            setExecutionTimeout(prevtime);
        }
    }

    private int getMaxTime() {
        final Query cq = getEntityManager().createNativeQuery(Queries.FETCH_MAX_EXECUTION_TIME);
        return ((Number)cq.getSingleResult()).intValue();
    }

    private void setExecutionTimeout(long timeout) {
        getEntityManager().createNativeQuery(Queries.SET_MAX_EXECUTION_TIME + timeout).executeUpdate();
    }

    private void setPagination(final TypedQuery<E> typedQuery) {
        if (perPage != null) {
            typedQuery.setMaxResults(getPerPage());
            if (pageNo != null) {
                typedQuery.setFirstResult((getPageNo() - 1) * getPerPage());
            }
        }
    }

    public E uniqueResult() {
        final TypedQuery<E> typedQuery = prepareSelectTypedQuery();
        return typedQuery.getSingleResult();
    }

    public Optional<E> first() {
        return setPageNo(1).setPerPage(1).list().stream().findFirst();
    }

    private TypedQuery<E> prepareSelectTypedQuery() {
        getCriteriaQuery().select(getRoot()).distinct(true);
        getCriteriaQuery().where(getPredicates().toArray(new Predicate[] {})).orderBy(orders);
        return getEntityManager().createQuery(getCriteriaQuery());
    }

    public JPAQueryBuilder<E> setPerPage(final Integer perPage) {
        this.perPage = perPage;
        return this;
    }

    public JPAQueryBuilder<E> setPageNo(final Integer pageNo) {
        this.pageNo = pageNo;
        return this;
    }

    public JPAQueryBuilder<E> orderBy(final String path, final boolean ascending) {
        orders.add(ascending ? getCriteriaBuilder().asc(getPath(path)) : getCriteriaBuilder().desc(getPath(path)));
        return this;
    }

    public JPAQueryBuilder<E> withTimeout(final QueryTimeout timeout) {
        this.timeout = timeout.getTimeoutSeconds();
        return this;
    }

}
