package org.example.bms.entity;

import lombok.Data;

@Data
public class Vehicle {
    private Integer id;
    private String cid;
    private String batteryType;
    private Double totalMileage;
    private Integer batteryHealth;
} 