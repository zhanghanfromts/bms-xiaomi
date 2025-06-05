package org.example.bms.service.impl;

import org.example.bms.entity.SignalRequest;
import org.example.bms.entity.VehicleSignalDTO;
import org.example.bms.entity.VehicleWarn;
import org.example.bms.entity.VehicleWarnDTO;
import org.example.bms.mapper.VehicleWarnMapper;
import org.example.bms.service.SignalScanService;
import org.example.bms.service.VehicleWarnService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@Transactional
public class VehicleWarnServiceImplTest {

    @Autowired
    private VehicleWarnService vehicleWarnService;

    private static final String TEST_CID = "1";

    @Test
    void testQueryWarns(){
        // 执行测试
        List<VehicleWarnDTO> vehicleWarnDTOList = vehicleWarnService.queryWarns(TEST_CID);
        // 验证结果
        assertNotNull(vehicleWarnDTOList);
        assertFalse(vehicleWarnDTOList.isEmpty());
        assertEquals(TEST_CID, vehicleWarnDTOList.get(0).getCid());
    }

    @Test
    void testQueryWarnsNotFound() {
        // 执行测试
        List<VehicleWarnDTO> result = vehicleWarnService.queryWarns("not_exist_cid");
        // 验证结果
        assertNull(result);
    }

} 