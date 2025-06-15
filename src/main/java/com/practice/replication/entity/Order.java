package com.practice.replication.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ToString
@Getter
@NoArgsConstructor
@Entity
@Table(name = "`order`")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Order(BigDecimal amount) {
        this.amount = amount;
        this.createdAt = LocalDateTime.now();
    }

}
