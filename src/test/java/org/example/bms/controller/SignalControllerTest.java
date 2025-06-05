package org.example.bms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.bms.entity.SignalRequest;
import org.example.bms.service.VehicleSignalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
public class SignalControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private VehicleSignalService vehicleSignalService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private static final String TEST_CID = "test001";
    private static final String TEST_SIGNAL_DATA = "{\"Mx\":12.0,\"Mi\":12.6}";

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testReportSignals_Success() throws Exception {
        // 准备测试数据
        List<SignalRequest> requests = new ArrayList<>();
        SignalRequest request = new SignalRequest();
        request.setCid(TEST_CID);
        request.setWarnId(1);
        request.setSignalData(TEST_SIGNAL_DATA);
        requests.add(request);

        // 执行测试
        mockMvc.perform(post("/api/signal/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requests)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("ok"));
    }

    @Test
    public void testReportSignals_EmptyList() throws Exception {
        // 准备测试数据
        List<SignalRequest> requests = new ArrayList<>();

        // 执行测试
        mockMvc.perform(post("/api/signal/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requests)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("信号上报失败"));
    }

    @Test
    public void testReportSignals_InvalidData() throws Exception {
        // 准备测试数据
        List<SignalRequest> requests = new ArrayList<>();
        SignalRequest request = new SignalRequest();
        // 不设置cid，模拟无效数据
        request.setSignalData("{\"Mx\":12.0,\"Mi\":12.6}");
        requests.add(request);

        // 执行测试
        mockMvc.perform(post("/api/signal/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requests)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("信号上报失败"));
    }

    @Test
    public void testQuerySignalStatus_Success() throws Exception {
        // 先插入测试数据
        List<SignalRequest> requests = new ArrayList<>();
        SignalRequest request = new SignalRequest();
        request.setCid("test001");
        request.setWarnId(1);
        request.setSignalData("{\"Mx\":12.0,\"Mi\":12.6}");
        requests.add(request);
        vehicleSignalService.reportSignals(requests);

        // 执行测试
        mockMvc.perform(get("/api/signal/test001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("ok"))
                .andExpect(jsonPath("$.data[0].cid").value("test001"))
                .andExpect(jsonPath("$.data[0].signalData").value("{\"Mx\":12.0,\"Mi\":12.6}"));
    }

    @Test
    public void testQuerySignalStatus_NotFound() throws Exception {
        // 执行测试
        mockMvc.perform(get("/api/signal/10000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("未找到信号数据"));
    }

    @Test
    public void testQuerySignalStatus_InvalidCid() throws Exception {
        // 执行测试
        mockMvc.perform(get("/api/signal/"))
                .andExpect(status().isNotFound());
    }
} 