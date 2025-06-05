package org.example.bms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.bms.entity.SignalRequest;
import org.example.bms.entity.VehicleSignal;
import org.example.bms.entity.VehicleSignalDTO;

import java.util.List;

public interface VehicleSignalService {
    /**
     * 批量上报电池信号（分库分表）
     * @param requests 信号请求列表
     * @return 是否上报成功
     */
    boolean reportSignals(List<SignalRequest> requests);

    /**
     * 查询指定车辆的最新10条信号
     */
    List<VehicleSignalDTO> queryLatestSignal(String cid);

    boolean saveSignal(VehicleSignal vehicleSignal) throws JsonProcessingException;
} 