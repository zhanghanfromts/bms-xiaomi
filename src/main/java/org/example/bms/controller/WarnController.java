package org.example.bms.controller;

import org.example.bms.common.Result;
import org.example.bms.entity.VehicleWarnDTO;
import org.example.bms.service.VehicleWarnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warn")
public class WarnController {

    @Autowired
    private VehicleWarnService vehicleWarnService;

    @GetMapping("/{cid}")
    public Result<List<VehicleWarnDTO>> queryWarns(@PathVariable String cid) {
        try {
            List<VehicleWarnDTO> warns = vehicleWarnService.queryWarns(cid);
            if(warns != null) {
                return Result.success(warns);
            }else{
                return Result.error(200,"无预警信息");
            }
        } catch (Exception e) {
            return Result.error("系统错误：" + e.getMessage());
        }
    }
} 