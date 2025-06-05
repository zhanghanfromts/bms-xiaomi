CREATE TABLE vehicle (
                         id INT unsigned AUTO_INCREMENT  PRIMARY KEY COMMENT '主键自增',
                         cid VARCHAR(17) UNIQUE NOT NULL COMMENT '车架编号，车辆唯一标识',
                         battery_type VARCHAR(17) NOT NULL COMMENT '电池类型：三元电池, 铁锂电池',
                         total_mileage DOUBLE NOT NULL DEFAULT 0 COMMENT '总里程(km)',
                         battery_health TINYINT UNSIGNED NOT NULL DEFAULT 100 COMMENT '电池健康状态(%)',
                         INDEX `idx_cid_battery_type` (`cid`, `battery_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆信息表';


INSERT INTO vehicle (cid, battery_type, total_mileage, battery_health) VALUES
                                                                           ('1', '三元电池', 100, 100),
                                                                           ('2', '铁锂电池', 600, 95),
                                                                           ('3', '三元电池', 300, 98),
                                                                           ('test001', '铁锂电池', 300, 98);

select * from vehicle;

CREATE TABLE `vehicle_signal` (
                                  `id` BIGINT AUTO_INCREMENT COMMENT '主键',
                                  `cid` VARCHAR(16) NOT NULL COMMENT '车辆识别码',
                                  `report_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '信号上报时间',
                                  `signal_data` JSON NOT NULL COMMENT '信号数据（Mx/Mi/Ix/Ii等）',
                                  `warn_id` INT NOT NULL COMMENT '规则编号',
                                  PRIMARY KEY (`id`),
                                  INDEX `idx_warn_id` (`warn_id`),
                                  INDEX `idx_report_time` (`report_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆信号上报表';

INSERT INTO `vehicle_signal` (`cid`, `report_time`, `signal_data`, `warn_id`)
VALUES (1, '2025-06-04 10:00:00', '{"Mx": 12.0, "Mi": 0.6}', 1);

INSERT INTO `vehicle_signal` (`cid`, `report_time`, `signal_data`, `warn_id`)
VALUES (2, '2025-06-04 10:05:00', '{"Ix": 12.0, "Ii": 11.7}', 2);

INSERT INTO `vehicle_signal` (`cid`, `report_time`, `signal_data`, `warn_id`)
VALUES (3, '2025-06-04 10:10:00', '{"Mx": 11.0, "Mi": 9.6, "Ix": 12.0, "Ii": 11.7}', -1);

select * from vehicle_signal

CREATE TABLE `rule_config` (
                               `id` INT AUTO_INCREMENT COMMENT '规则序号（主键）',
                               `warn_id` INT NOT NULL COMMENT '规则编号（如1=电压差报警，2=电流差报警）',
                               `warn_name` VARCHAR(50) NOT NULL COMMENT '规则名称',
                               `battery_type` VARCHAR(20) NOT NULL COMMENT '电池类型（三元电池/铁锂电池）',
                               `rule_expression` TEXT NOT NULL COMMENT '预警规则表达式（含阈值和报警等级）',
                               PRIMARY KEY (`id`),
                               UNIQUE INDEX `uniq_type_warn_id` (`battery_type`, `warn_id`), -- 组合唯一索引：同一规则编号+电池类型唯一
                               INDEX `idx_battery_type` (`battery_type`),
                               INDEX `idx_warn_id` (`warn_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预警规则配置表';
INSERT INTO `rule_config` (`warn_id`, `warn_name`, `battery_type`, `rule_expression`)
VALUES (1, '电压差报警', '三元电池', '5<=(Mx−Mi),0\n3<=(Mx−Mi)<5,1\n1<=(Mx−Mi)<3,2\n0.6<=(Mx−Mi)<1,3\n0.2<=(Mx−Mi)<0.6,4\n(Mx−Mi)<0.2,-1');
INSERT INTO `rule_config` (`warn_id`, `warn_name`, `battery_type`, `rule_expression`)
VALUES (1, '电压差报警', '铁锂电池', '2<=(Mx−Mi),0\n1<=(Mx−Mi)<2,1\n0.7<=(Mx−Mi)<1,2\n0.4<=(Mx−Mi)<0.7,3\n0.2<=(Mx−Mi)<0.4,4\n(Mx−Mi)<0.2,-1');
INSERT INTO `rule_config` (`warn_id`, `warn_name`, `battery_type`, `rule_expression`)
VALUES (2, '电流差报警', '三元电池', '3<=(Ix−Ii),0\n1<=(Ix−Ii)<3,1\n0.2<=(Ix−Ii)<1,2\n(Ix−Ii)<0.2,-1');
INSERT INTO `rule_config` (`warn_id`, `warn_name`, `battery_type`, `rule_expression`)
VALUES (2, '电流差报警', '铁锂电池', '1<=(Ix−Ii),0\n0.5<=(Ix−Ii)<1,1\n0.2<=(Ix−Ii)<0.5,2\n(Ix−Ii)<0.2,-1');

select * from rule_config

CREATE TABLE vehicle_warn (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                              cid VARCHAR(50) NOT NULL COMMENT '车架编号',
                              battery_type VARCHAR(50) NOT NULL COMMENT '电池类型',
                              warn_id INT NOT NULL COMMENT '告警ID',
                              warn_name VARCHAR(100) NOT NULL COMMENT '告警名称',
                              warn_level INT NOT NULL COMMENT '告警等级',
                              warn_time DATETIME NOT NULL COMMENT '告警时间',
                              signal_data TEXT NOT NULL COMMENT '信号数据',
                              INDEX idx_cid (cid),
                              INDEX idx_warn_time (warn_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆告警记录表';
select * from vehicle_warn;

CREATE TABLE `vehicle_signal_20250604` (
                                           `id` BIGINT AUTO_INCREMENT COMMENT '主键',
                                           `cid` VARCHAR(16) NOT NULL COMMENT '车辆识别码',
                                           `report_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '信号上报时间',
                                           `signal_data` JSON NOT NULL COMMENT '信号数据（Mx/Mi/Ix/Ii等）',
                                           `warn_id` INT NOT NULL COMMENT '规则编号',
                                           PRIMARY KEY (`id`),
                                           INDEX `idx_warn_id` (`warn_id`),
                                           INDEX `idx_report_time` (`report_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆信号上报表';

select * from vehicle_signal_20250605

