package com.kamko.bankdemo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@ToString(exclude = "transactions")
@EqualsAndHashCode(exclude = "transactions")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String pin;

    private BigDecimal balance;

    @OneToMany(mappedBy = "account")
    private List<Transaction> transactions;

}
