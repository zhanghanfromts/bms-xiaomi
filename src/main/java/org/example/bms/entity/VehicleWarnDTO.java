package org.example.bms.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class VehicleWarnDTO {
    private String cid;
    private String warnName;
    private Integer warnLevel;
    private String signalData;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date warnTime;
}