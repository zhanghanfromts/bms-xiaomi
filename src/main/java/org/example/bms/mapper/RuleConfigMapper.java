package org.example.bms.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.bms.entity.RuleConfig;
import java.util.List;

@Mapper
public interface RuleConfigMapper {
    /**
     * 根据电池类型获取规则配置
     * @param batteryType 电池类型
     * @return 规则配置列表
     */
    List<RuleConfig> selectByBatteryType(@Param("batteryType") String batteryType);
    
    /**
     * 根据规则编号和电池类型获取规则配置
     */
    RuleConfig selectByWarnIdAndBatteryType(@Param("warnId") Integer warnId, @Param("batteryType") String batteryType);
} 