package com.example.getirme.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("RESTAURANT")
public class Restaurant extends User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalTime openingTime;

    private LocalTime closingTime;

    private Integer maxServiceDistance;

    private Integer minServicePricePerKm;

    @OneToMany
    private List<Product> products;

    @OneToOne
    private FileEntity image;

    public void addProduct(Product product){
        this.products.add(product);
    }

}
