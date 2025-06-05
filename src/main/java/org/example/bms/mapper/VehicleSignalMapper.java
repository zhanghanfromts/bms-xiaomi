package org.example.bms.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.bms.entity.VehicleSignal;

import java.util.Date;
import java.util.List;

@Mapper
public interface VehicleSignalMapper {
    /**
     * 插入单条信号数据
     */
    int insert(@Param("signal") VehicleSignal signal, @Param("tableSuffix") String tableSuffix);
    
    /**
     * 批量插入信号数据
     */
    int batchInsert(@Param("list") List<VehicleSignal> signals, @Param("tableSuffix") String tableSuffix);
    
    /**
     * 查询指定车辆的最新10条信号
     */
    List<VehicleSignal> selectLatestSignal(@Param("cid") String cid, @Param("tableSuffix") String tableSuffix);
    
    /**
     * 查询指定时间范围内的最新信号
     */
    List<VehicleSignal> selectLatestSignals(@Param("startTime") Date startTime, @Param("tableSuffix") String tableSuffix);
} 