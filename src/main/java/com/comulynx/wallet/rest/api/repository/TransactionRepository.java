package com.comulynx.wallet.rest.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.comulynx.wallet.rest.api.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	Optional<List<Transaction>> findTransactionsByCustomerId(String customerId);

	Optional<List<Transaction>> findTransactionsByTransactionId(String transactionId);

	Optional<List<Transaction>> findTransactionsByCustomerIdOrTransactionId(String transactionId, String customerId);

	// Custom query to return transactions for a given customerId and accountNo


	@Query(value = "SELECT t FROM Transaction t WHERE t.customerId = :customerId AND t.accountNo = :accountNo ORDER BY t.id DESC")
	Optional<List<Transaction>> getMiniStatementUsingCustomerIdAndAccountNo(
			@Param("customerId") String customerId,
			@Param("accountNo") String accountNo
	);

}
