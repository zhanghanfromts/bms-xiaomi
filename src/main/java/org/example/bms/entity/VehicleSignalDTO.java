package org.example.bms.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;

@Data
public class VehicleSignalDTO {
    private String cid;           // 车架编号
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date reportTime;      // 上报时间
    private String signalData;    // 信号数据
    private Integer warnId;       // 信号类型
} 