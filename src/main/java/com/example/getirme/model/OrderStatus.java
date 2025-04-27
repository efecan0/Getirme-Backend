package com.example.getirme.model;

public enum OrderStatus {
    PENDING, // Sipariş verildi
    PREPARING, // Hazırlanıyor
    ON_THE_WAY, // Yolda
    DELIVERED, // Teslim edildi
    CANCELLED // İptal edildi
}
