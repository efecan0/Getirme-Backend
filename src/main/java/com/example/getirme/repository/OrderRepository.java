package com.example.getirme.repository;

import com.example.getirme.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<List<Order>> findByRestaurantId(Long id);
    Optional<List<Order>> findByCustomerId(Long id);
}
