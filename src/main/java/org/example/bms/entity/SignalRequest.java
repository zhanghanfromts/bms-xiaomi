package org.example.bms.entity;

import lombok.Data;

@Data
public class SignalRequest {
    private String cid;
    private Integer warnId;
    private String signalData;
} 