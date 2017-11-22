package com.jadyer.seed.comm.jpa;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Range;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ---------------------------------------------------------------------------------------------------------------
 * 用法如下：通过or()或者and()方法来创建实例，然后调用各个查询条件，即可
 * Condition<User> spec = Condition.or();
 * Condition<User> spec = Condition.and();
 * spec.eq("uid", uid);
 * spec.between("updateTime", new org.springframework.data.domain.Range<>(new Date(), new Date()));
 * Condition<User> spec = Condition.<User>or().eq("id", 8).notIn("name", nameList);
 * userRepository.findAll(spec, new PageRequest(0, 15, new Sort(Sort.Direction.DESC, "id")));
 * ---------------------------------------------------------------------------------------------------------------
 * Created by 玄玉<http://jadyer.cn/> on 2016/7/2 17:11.
 */
public class Condition<T> implements Specification<T> {
    private Predicate.BooleanOperator operatorType = Predicate.BooleanOperator.AND;

    private enum Operator{
        EQ, NE, GT, LT, GE, LE, BETWEEN, IN, NOTIN, LIKE, NOTLIKE
    }

    private List<SearchFilter> filters = new ArrayList<>();

    private class SearchFilter {
        String property;
        Operator operator;
        Object value;
        SearchFilter(String property, Operator operator, Object value) {
            this.property = property;
            this.operator = operator;
            this.value = value;
        }
    }

    private Condition(Predicate.BooleanOperator operatorType) {
        this.operatorType = operatorType;
    }

    public static <T> Condition<T> or() {
        return new Condition<>(Predicate.BooleanOperator.OR);
    }

    public static <T> Condition<T> and() {
        return new Condition<>(Predicate.BooleanOperator.AND);
    }

    private Condition<T> add(String property, Operator operator, Object value) {
        filters.add(new SearchFilter(property, operator, value));
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        if (filters.isEmpty()) {
            return cb.conjunction();
        }
        List<Predicate> predicateList = new ArrayList<>();
        for (SearchFilter filter : filters) {
            String[] propertys = StringUtils.split(filter.property, ".");
            Path expression = root.get(propertys[0]);
            for (int i=1; i<propertys.length; i++) {
                expression = expression.get(propertys[i]);
            }
            switch (filter.operator) {
                case EQ:
                        if(null == filter.value){
                            predicateList.add(cb.isNull(expression));
                        }else{
                            predicateList.add(cb.equal(expression, filter.value));
                        }
                        break;
                case NE:
                        if(null == filter.value){
                            predicateList.add(cb.isNotNull(expression));
                        }else{
                            predicateList.add(cb.notEqual(expression, filter.value));
                        }
                        break;
                case GT:
                        predicateList.add(cb.gt(expression, (Number)filter.value));
                        break;
                case LT:
                        predicateList.add(cb.lt(expression, (Number)filter.value));
                        break;
                case GE:
                        predicateList.add(cb.ge(expression, (Number)filter.value));
                        break;
                case LE:
                        predicateList.add(cb.le(expression, (Number)filter.value));
                        break;
                case BETWEEN:
                        Range range = (Range)filter.value;
                        predicateList.add(cb.between(expression, range.getLowerBound(), range.getUpperBound()));
                        break;
                case IN:
                        predicateList.add(cb.in(expression).value(filter.value));
                        break;
                case NOTIN:
                        predicateList.add(cb.in(expression).value(filter.value).not());
                        break;
                case LIKE:
                        List valueList = (List)filter.value;
                        Predicate[] predicates = new Predicate[valueList.size()];
                        for(int i=0; i<valueList.size(); i++){
                            predicates[i] = cb.like(expression, "%" + valueList.get(i) + "%");
                        }
                        predicateList.add(cb.or(predicates));
                        break;
                case NOTLIKE:
                        valueList = (List)filter.value;
                        predicates = new Predicate[valueList.size()];
                        for(int i=0; i<valueList.size(); i++){
                            predicates[i] = cb.notLike(expression, "%" + valueList.get(i) + "%");
                        }
                        predicateList.add(cb.and(predicates));
                        break;
                default:
                        System.out.println("nothing to do");
            }
        }
        Predicate[] predicates = predicateList.toArray(new Predicate[predicateList.size()]);
        return Predicate.BooleanOperator.AND.equals(operatorType) ? cb.and(predicates) : cb.or(predicates);
    }

    public Condition<T> eq(String property, Object value) {
        this.add(property, Operator.EQ, value);
        return this;
    }

    public Condition<T> ne(String property, Object value) {
        this.add(property, Operator.NE, value);
        return this;
    }

    public Condition<T> gt(String property, Number number) {
        this.add(property, Operator.GT, number);
        return this;
    }

    public Condition<T> lt(String property, Number number) {
        this.add(property, Operator.LT, number);
        return this;
    }

    public Condition<T> ge(String property, Number number) {
        this.add(property, Operator.GE, number);
        return this;
    }

    public Condition<T> le(String property, Number number) {
        this.add(property, Operator.LE, number);
        return this;
    }

    public Condition<T> between(String property, Range<? extends Comparable<?>> range) {
        this.add(property, Operator.BETWEEN, range);
        return this;
    }

    public Condition<T> in(String property, List valueList) {
        this.add(property, Operator.IN, valueList);
        return this;
    }

    public Condition<T> notIn(String property, List valueList) {
        this.add(property, Operator.NOTIN, valueList);
        return this;
    }

    public Condition<T> like(String property, List<String> valueList) {
        this.add(property, Operator.LIKE, valueList);
        return this;
    }

    public Condition<T> like(String property, String value) {
        return this.like(property, Collections.singletonList(value));
    }

    public Condition<T> notLike(String property, String value) {
        return this.notLike(property, Collections.singletonList(value));
    }

    public Condition<T> notLike(String property, List<String> valueList) {
        this.add(property, Operator.NOTLIKE, valueList);
        return this;
    }
}