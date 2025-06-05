package org.example.bms.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.bms.entity.VehicleWarn;

import java.util.List;

@Mapper
public interface VehicleWarnMapper {
    /**
     * 插入告警记录
     * @param warn 告警信息
     * @return 影响行数
     */
    int insert(VehicleWarn warn);

    /**
     * 根据车架编号查询预警信息
     * @param cid 车架编号
     * @return 预警信息列表
     */
    List<VehicleWarn> selectByCid(@Param("cid") String cid);
} 