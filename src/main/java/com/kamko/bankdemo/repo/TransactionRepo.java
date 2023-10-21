package com.kamko.bankdemo.repo;

import com.kamko.bankdemo.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepo extends JpaRepository<Transaction, Long> {

    @Query("""
            select t
            from Transaction t
            where t.account.id = :accountId
            """)
    Page<Transaction> findPageOfTransaction(Pageable pageable, @Param(value = "accountId") Long accountId);

}
