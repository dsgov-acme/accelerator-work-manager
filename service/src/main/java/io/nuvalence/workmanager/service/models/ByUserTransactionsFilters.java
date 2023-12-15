package io.nuvalence.workmanager.service.models;

import io.nuvalence.workmanager.service.domain.transaction.Transaction;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.Predicate;

/**
 * The filters to filter the transactions by.
 */
@Getter
@Setter
public class ByUserTransactionsFilters extends TransactionFilters {

    /**
     * Builder for TransactionFilters.
     *
     * @param subjectUserId            The subjectUserId to filter transactions by
     * @param createdBy                The createdBy user id to filter transactions by
     * @param transactionDefinitionKey The transaction definition key to filter transactions by
     * @param isCompleted              The isCompleted to filter transactions by
     * @param status                   The status to filter transactions by
     * @param sortBy                   The column to filter transactions by
     * @param sortOrder                The order to filter transactions by
     * @param pageNumber                     The number of the pages to get transactions
     * @param pageSize                     The number of transactions per pageNumber
     */
    @Builder
    public ByUserTransactionsFilters(
            String subjectUserId,
            String createdBy,
            String transactionDefinitionKey,
            Boolean isCompleted,
            List<String> status,
            String sortBy,
            String sortOrder,
            Integer pageNumber,
            Integer pageSize) {
        super(sortBy, sortOrder, pageNumber, pageSize);
        this.setSubjectUserId(subjectUserId);
        this.setCreatedBy(createdBy);
        this.setIsCompleted(isCompleted);
        this.setStatus(status);

        if (transactionDefinitionKey != null) {
            this.setTransactionDefinitionKeys(List.of(transactionDefinitionKey));
        }
    }

    @Override
    public Specification<Transaction> getTransactionSpecifications() {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> orPredicates = new ArrayList<>();
            List<Predicate> andPredicates = new ArrayList<>();

            addEqualPredicate(
                    orPredicates, criteriaBuilder, root.get("subjectUserId"), getSubjectUserId());

            addEqualPredicate(orPredicates, criteriaBuilder, root.get("createdBy"), getCreatedBy());

            addEqualPredicate(
                    andPredicates, criteriaBuilder, root.get("isCompleted"), getIsCompleted());

            addInPredicate(andPredicates, root.get("status"), getStatus());

            addInPredicateWithEmptySupport(
                    andPredicates,
                    root.get("transactionDefinitionKey"),
                    getTransactionDefinitionKeys());

            Predicate orPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[0]));
            Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[0]));

            // Combine OR and AND with the top-level AND
            Predicate finalPredicate = criteriaBuilder.and(orPredicate, andPredicate);

            return finalPredicate;
        };
    }
}
