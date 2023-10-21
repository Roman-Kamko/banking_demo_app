package com.kamko.bankdemo.repo;

import com.kamko.bankdemo.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepo extends JpaRepository<Account, Long> {
}
