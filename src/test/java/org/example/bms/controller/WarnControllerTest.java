package org.example.bms.controller;

import org.example.bms.service.VehicleWarnService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
public class WarnControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testQueryWarnStatus_Success() throws Exception {
        // 执行测试
        mockMvc.perform(get("/api/warn/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("ok"));
    }

    @Test
    public void testQueryWarnStatus_NotFound() throws Exception {
        // 执行测试
        mockMvc.perform(get("/api/warn/10000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("无预警信息"));
    }

    @Test
    public void testQueryWarnStatus_InvalidId() throws Exception {
        // 执行测试
        mockMvc.perform(get("/api/warn/"))
                .andExpect(status().isNotFound());
    }
} 