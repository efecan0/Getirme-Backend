package com.example.getirme.repository;

import com.example.getirme.model.SelectableContentOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SelectableContentOptionRepository extends JpaRepository<SelectableContentOption, Long> {
Optional<SelectableContentOption> findByNameAndPrice(String name , Double price);

}
