package com.comulynx.wallet.rest.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.comulynx.wallet.rest.api.model.Customer;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByCustomerId(String customerId);


    Optional<Customer> findByEmail(String email);


    /**
     * TODO : Implement the query and function below to delete a customer using Customer
     * <p>
     * <p>
     * Transactional  ensures that the delete operation occurs within a transaction,
     * providing consistency and rollback support if any issues occur during execution.
     * Modifying is required for any update or delete operation in Spring Data JPA,
     * allowing it to modify data in the database.
     * This method deletes a customer record based on a unique customerId, helping to
     * manage customer data by removing specific entries as needed.
     *
     * @param customerId
     */

    // @Transactional ensures that the delete operation occurs within a transaction,
    // providing consistency and rollback support if any issues occur during execution.
    // @Modifying is required for any update or delete operation in Spring Data JPA,
    // allowing it to modify data in the database.
    // This method deletes a customer record based on a unique customerId, helping to
    // manage customer data by removing specific entries as needed.
    @Transactional
    @Modifying
    @Query("DELETE FROM Customer c WHERE c.customerId = :customerId")
    void deleteByCustomerId(String customerId);

    /**
     * TODO : Implement the query and function below to update customer firstName using Customer Id
     * <p>
     * <p>
     * This method updates a customer's firstName record based on a unique customerId
     *
     * @param firstName
     * @param customerId
     */

    @Transactional
    @Modifying
    @Query("UPDATE Customer c SET c.firstName = :firstName WHERE c.customerId = :customerId")
    int updateFirstNameByCustomerId(@Param("firstName") String firstName, @Param("customerId") String customerId);

    /**
     * TODO : Implement the query and function below and to return all customers whose Email contains  'gmail'
     * <p>
     * <p>
     * This method finds all customers whose email contains 'gmail'
     *
     * @param emailDomain
     *
     */
    @Query("SELECT c FROM Customer c WHERE c.email LIKE %:emailDomain%")
    List<Customer> findByEmailContaining(@Param("emailDomain") String emailDomain);
}
