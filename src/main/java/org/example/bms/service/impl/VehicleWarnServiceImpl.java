package org.example.bms.service.impl;

import org.example.bms.entity.VehicleSignal;
import org.example.bms.entity.VehicleSignalDTO;
import org.example.bms.entity.VehicleWarn;
import org.example.bms.entity.VehicleWarnDTO;
import org.example.bms.mapper.VehicleWarnMapper;
import org.example.bms.service.VehicleWarnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VehicleWarnServiceImpl implements VehicleWarnService {

    @Autowired
    private VehicleWarnMapper vehicleWarnMapper;

    @Override
    public List<VehicleWarnDTO> queryWarns(String cid) {
        List<VehicleWarn> vehicleWarns = vehicleWarnMapper.selectByCid(cid);
        // 构建响应列表
        List<VehicleWarnDTO> responseList = new ArrayList<>();
        for (VehicleWarn vehicleWarn : vehicleWarns) {
            VehicleWarnDTO response = new VehicleWarnDTO();
            response.setCid(vehicleWarn.getCid());
            response.setWarnName(vehicleWarn.getWarnName());
            response.setWarnLevel(vehicleWarn.getWarnLevel());
            response.setSignalData(vehicleWarn.getSignalData());
            response.setWarnTime(vehicleWarn.getWarnTime());
            responseList.add(response);
        }
        return responseList.isEmpty() ? null : responseList;
    }
} 