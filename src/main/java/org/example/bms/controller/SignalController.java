package org.example.bms.controller;

import org.example.bms.common.Result;
import org.example.bms.entity.SignalRequest;
import org.example.bms.entity.VehicleSignalDTO;
import org.example.bms.service.VehicleSignalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/signal")
public class SignalController {

    @Autowired
    private VehicleSignalService vehicleSignalService;

    /**
     * 批量上报信号（分库分表）
     */
    @PostMapping("/batch")
    public Result<Void> reportSignals(@RequestBody List<SignalRequest> requests) {
        boolean success = vehicleSignalService.reportSignals(requests);
        if (success) {
            return Result.success();
        } else {
            return Result.error("信号上报失败");
        }
    }

    /**
     * 查询最新的10条信号状态
     */
    @GetMapping("/{cid}")
    public Result<List<VehicleSignalDTO>> querySignalStatus(@PathVariable String cid) {
        List<VehicleSignalDTO> signals = vehicleSignalService.queryLatestSignal(cid);
        if (signals != null) {
            return Result.success(signals);
        } else {
            return Result.error("未找到信号数据");
        }
    }
} 