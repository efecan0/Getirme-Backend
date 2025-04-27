package com.example.getirme.repository;

import com.example.getirme.model.Order;
import com.example.getirme.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<List<Order>> findByRestaurantId(Long id);
    Optional<List<Order>> findByCustomerId(Long id);
    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId AND o.status NOT IN (:excludedStatuses)")
    Optional<List<Order>> findActiveOrdersByCustomerId(
            @Param("customerId") Long customerId,
            @Param("excludedStatuses") List<OrderStatus> excludedStatuses
    );

    @Query("SELECT o FROM Order o WHERE o.restaurant.id = :restaurantId AND o.status NOT IN (:excludedStatuses)")
    Optional<List<Order>> findActiveOrdersByRestaurantId(
            @Param("restaurantId") Long restaurantId,
            @Param("excludedStatuses") List<OrderStatus> excludedStatuses
    );
}
