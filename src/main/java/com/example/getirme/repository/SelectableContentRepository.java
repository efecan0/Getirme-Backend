package com.example.getirme.repository;

import com.example.getirme.model.SelectableContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SelectableContentRepository extends JpaRepository<SelectableContent, Long> {
    Optional<SelectableContent> findByName(String name);
}
