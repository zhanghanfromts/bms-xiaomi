package org.example.bms.service;

import org.example.bms.entity.VehicleWarn;
import org.example.bms.entity.VehicleWarnDTO;

import java.util.List;

public interface VehicleWarnService {
    /**
     * 查询车辆的预警信息
     * @param cid 车架编号
     * @return 预警信息列表
     */
    List<VehicleWarnDTO> queryWarns(String cid);
} 