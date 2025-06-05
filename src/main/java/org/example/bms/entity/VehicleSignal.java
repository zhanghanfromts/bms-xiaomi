package org.example.bms.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;
import java.text.SimpleDateFormat;

@Data
public class VehicleSignal {
    private Long id;
    private String cid;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date reportTime;
    private String signalData;
    private Integer warnId;

    /**
     * 获取分表后缀
     * 按天分表，格式：yyyyMMdd
     */
    public String getTableSuffix() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(reportTime);
    }

} 