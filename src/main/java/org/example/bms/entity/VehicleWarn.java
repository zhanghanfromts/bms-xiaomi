package org.example.bms.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;

@Data
public class VehicleWarn {
    private Long id;
    private String cid;
    private String batteryType;
    private Integer warnId;
    private String warnName;
    private Integer warnLevel;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date warnTime;
    private String signalData;
} 