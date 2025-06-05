package org.example.bms.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.bms.entity.Vehicle;

@Mapper
public interface VehicleMapper {
    /**
     * 根据车架编号查询车辆信息
     */
    Vehicle selectByCid(@Param("cid") String cid);

    /**
     * 插入车辆信息
     * @param vehicle 车辆信息
     * @return 影响行数
     */
    int insert(@Param("vehicle") Vehicle vehicle);
} 