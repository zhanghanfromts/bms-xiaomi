package org.example.bms.service;

public interface SignalScanService {
    /**
     * 扫描信号数据
     * 每30秒执行一次,扫描最近30秒内的信号数据
     */
    void scanAndSendSignals();
} 