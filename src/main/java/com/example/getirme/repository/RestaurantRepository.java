package com.example.getirme.repository;

import com.example.getirme.model.FileEntity;
import com.example.getirme.model.Restaurant;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    @Transactional
    @Modifying
    @Query("UPDATE Restaurant r SET " +
            "r.name = :name, " +
            "r.phoneNumber = :phoneNumber, " +
            "r.location = :location, " +
            "r.password = :password, " +
            "r.openingTime = :openingTime, " +
            "r.closingTime = :closingTime, " +
            "r.maxServiceDistance = :maxServiceDistance, " +
            "r.minServicePricePerKm = :minServicePricePerKm, " +
            "r.image = :image " +
            "WHERE r.id = :id")
    void updateRestaurant(@Param("id") Long id,
                          @Param("name") String name,
                          @Param("phoneNumber") String phoneNumber,
                          @Param("location") String location,
                          @Param("password") String password,
                          @Param("openingTime") LocalTime openingTime,
                          @Param("closingTime") LocalTime closingTime,
                          @Param("maxServiceDistance") Integer maxServiceDistance,
                          @Param("minServicePricePerKm") Integer minServicePricePerKm,
                          @Param("image") FileEntity image);
}
