package com.practice.replication.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;

import com.practice.replication.entity.Order;
import com.practice.replication.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderService {

    private static final Random RANDOM = new Random();

    private final OrderRepository orderRepository;

    @Transactional
    public Order createOrder() {
        BigDecimal randomValue = BigDecimal.valueOf(RANDOM.nextInt(10001))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        Order order = new Order(randomValue);
        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

}
