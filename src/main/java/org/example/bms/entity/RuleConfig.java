package org.example.bms.entity;

import lombok.Data;

@Data
public class RuleConfig {
    private Integer id;
    private Integer warnId;
    private String warnName;
    private String batteryType;
    private String ruleExpression;
} 