package com.kamko.bankdemo.repo;

import com.kamko.bankdemo.entity.TransactionLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionLogRepo extends JpaRepository<TransactionLog, Long> {

    @Query("""
            select t
            from TransactionLog t
            where t.account.id = :accountId
            """)
    Page<TransactionLog> findPageOfTransaction(Pageable pageable, @Param(value = "accountId") Long accountId);

}
