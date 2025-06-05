package org.example.bms.config;

import lombok.extern.slf4j.Slf4j;
import org.example.bms.service.SignalScanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Configuration
@EnableScheduling
public class ScheduleConfig {

    @Autowired
    private SignalScanService signalScanService;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private static final String TABLE_PREFIX = "vehicle_signal_";

    /**
     * 每30秒执行一次信号扫描 10*1000=10000
     */
    @Scheduled(fixedRate = 10000)
    public void scanSignals() {
        signalScanService.scanAndSendSignals();
    }
    
    /**
     * 每天凌晨0点执行，创建当天的表
     */
    @Scheduled(cron = "0 59 9 * * ?")  //秒 分 时 日 月 周 [年]
    public void createTodayTable() {
        log.info("开始执行创建当天表任务");
        try {
            createTable(new Date());
            log.info("创建当天表任务执行完成");
        } catch (Exception e) {
            log.error("创建当天表任务执行失败", e);
        }
    }
    
    /**
     * 创建指定日期的表
     * @param date 日期
     */
    private void createTable(Date date) {
        String tableSuffix = new SimpleDateFormat("yyyyMMdd").format(date);
        String tableName = TABLE_PREFIX + tableSuffix;
        
        try {
            // 检查表是否存在
            String checkTableSql = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = ?";
            Integer count = jdbcTemplate.queryForObject(checkTableSql, Integer.class, tableName);
            
            if (count == null || count == 0) {
                // 创建表
                String createTableSql = "CREATE TABLE " + tableName + " (" +
                    "id BIGINT PRIMARY KEY AUTO_INCREMENT," +
                    "cid VARCHAR(50) NOT NULL," +
                    "signal_data TEXT," +
                    "report_time DATETIME," +
                    "warn_id INT," +
                    "INDEX idx_cid (cid)," +
                    "INDEX idx_report_time (report_time)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
                
                jdbcTemplate.execute(createTableSql);
                log.info("成功创建表: {}", tableName);
            } else {
                log.info("表已存在: {}", tableName);
            }
        } catch (Exception e) {
            log.error("创建表失败: " + tableName, e);
            throw new RuntimeException("创建表失败: " + tableName, e);
        }
    }
} 