package com.example.getirme.repository;

import com.example.getirme.model.OrderSelectedContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderSelectedContentRepository extends JpaRepository<OrderSelectedContent , Long> {
}
