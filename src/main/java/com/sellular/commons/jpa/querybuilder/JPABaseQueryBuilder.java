package com.sellular.commons.jpa.querybuilder;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.metamodel.*;
import lombok.AccessLevel;
import lombok.Getter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.sql.Timestamp;

@Getter(AccessLevel.PACKAGE)
@SuppressWarnings ({ "rawtypes", "unchecked" })
public abstract class JPABaseQueryBuilder<E, R, T extends JPABaseQueryBuilder<E, R, T>> {

    private final EntityManager entityManager;

    private final Class<E> entityClass;

    private final CriteriaBuilder criteriaBuilder;

    private final CriteriaQuery<R> criteriaQuery;

    private final Root<E> root;

    protected abstract T self();

    private final List<Predicate> predicates = new ArrayList<>();

    protected JPABaseQueryBuilder(final EntityManager entityManager, final Class<E> entityClass, final Class<R> responseClass) {
        this.entityManager = entityManager;
        this.entityClass = entityClass;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
        this.criteriaQuery = criteriaBuilder.createQuery(responseClass);
        this.root = criteriaQuery.from(entityClass);
    }

    public T equal(final String path, final Object value) {
        predicates.add(criteriaBuilder.equal(getPath(path), value));
        return self();
    }

    public T notEqual(final String path, final Object value) {
        predicates.add(criteriaBuilder.notEqual(getPath(path), value));
        return self();
    }

    public T lessThanOrEqualTo(final String path, final Comparable value) {
        predicates.add(criteriaBuilder.lessThanOrEqualTo(getPath(path), value));
        return self();
    }

    public T lessThan(final String path, final Comparable value) {
        predicates.add(criteriaBuilder.lessThan(getPath(path), value));
        return self();
    }

    public T greaterThanOrEqualTo(final String path, final Comparable value) {
        predicates.add(criteriaBuilder.greaterThanOrEqualTo(getPath(path), value));
        return self();
    }

    public T greaterThan(final String path, final Comparable value) {
        predicates.add(criteriaBuilder.greaterThan(getPath(path), value));
        return self();
    }

    public T between(final String path, final Expression startValue, final Expression endValue) {
        predicates.add(criteriaBuilder.between(getPath(path), startValue, endValue));
        return self();
    }

    public T between(final String path, final Timestamp startValue, final Timestamp endValue) {
        predicates.add(criteriaBuilder.between(getPath(path), startValue, endValue));
        return self();
    }

    public T in(final String path, final Collection<?> collection) {
        predicates.add(criteriaBuilder.in(getPath(path)).value(collection));
        return self();
    }

    public T notIn(final String path, final Collection<?> collection) {
        predicates.add(criteriaBuilder.not(criteriaBuilder.in(getPath(path)).value(collection)));
        return self();
    }

    public T isNull(final String path) {
        predicates.add(criteriaBuilder.isNull(getPath(path)));
        return self();
    }

    public T isNotNull(final String path) {
        predicates.add(criteriaBuilder.isNotNull(getPath(path)));
        return self();
    }


    public T like(final String path, final String value) {
        predicates.add(criteriaBuilder.like(getPath(path), value));
        return self();
    }

    public T addDisjunction(final Predicate... predicates) {
        this.predicates.add(criteriaBuilder.or(predicates));
        return self();
    }

    public Predicate equalsPredicate(final String path, final Object value) {
        return criteriaBuilder.equal(getPath(path), value);
    }

    protected <Y> Path<Y> getPath(final String stringPath) {
        Path<?> path = root;
        From<?, ?> from = root;

        String[] pathSteps = stringPath.split("\\.");

        for (int i = 0; i < pathSteps.length; i++) {
            String step = pathSteps[i];
            ManagedType<?> managedType = entityManager.getMetamodel().managedType(path.getJavaType());
            Attribute<?, ?> attribute = managedType.getAttribute(step);

            if (attribute instanceof PluralAttribute) {
                PluralAttribute pluralAttribute = (PluralAttribute) attribute;
                Join<?, ?> join = getJoin(pluralAttribute, from);
                from = join;
                path = join;
            } else if (attribute instanceof SingularAttribute) {
                SingularAttribute singularAttribute = (SingularAttribute) attribute;
                if (singularAttribute.getPersistentAttributeType() != Attribute.PersistentAttributeType.BASIC) {
                    Join<?, ?> join = from.join(singularAttribute, JoinType.LEFT);
                    from = join;
                    path = join;
                } else {
                    path = path.get(step);
                }
            } else {
                path = path.get(step);
            }
        }

        Path<Y> typedPath = (Path<Y>) path;
        return typedPath;
    }

    private Join getJoin(final PluralAttribute attr, final From from) {
        final Set<?> joins = from.getJoins();
        for (Object object : joins) {
            Join<?, ?> join = (Join<?, ?>) object;
            if (join.getAttribute().getName().equals(attr.getName())) {
                return join;
            }
        }
        return createJoin(attr, from);
    }

    private Join createJoin(final PluralAttribute attr, final From from) {
        switch (attr.getCollectionType()) {
            case COLLECTION:
                return from.join((CollectionAttribute) attr);
            case SET:
                return from.join((SetAttribute) attr);
            case LIST:
                return from.join((ListAttribute) attr);
            case MAP:
                return from.join((MapAttribute) attr);
            default:
                return null;
        }
    }

}