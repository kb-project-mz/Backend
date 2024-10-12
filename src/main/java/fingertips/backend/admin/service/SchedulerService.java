package fingertips.backend.admin.service;

import fingertips.backend.admin.dto.UserMetricsAggregateDTO;
import fingertips.backend.admin.dto.UserMetricsDTO;
import fingertips.backend.admin.mapper.AdminMapper;
import fingertips.backend.exception.error.ApplicationError;
import fingertips.backend.exception.error.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class SchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(SchedulerService.class);

    @Autowired
    private AdminService adminService;

    @Value("${jdbc.url}")
    private String jdbcUrl;

    @Value("${jdbc.username}")
    private String dbUsername;

    @Value("${jdbc.password}")
    private String dbPassword;

    private static final String MYSQL_DUMP_PATH = "C:/Program Files/MySQL/MySQL Server 8.0/bin/mysqldump.exe";
    private static final String BACKUP_DIRECTORY = "C:\\backups\\";

    @Scheduled(cron = "0 0 0 * * ?")
    public void backupDatabase() {
        String backupFileName = "db_backup_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".sql";
        File backupFile = new File(BACKUP_DIRECTORY + backupFileName);

        try {
            String dbName = jdbcUrl.substring(jdbcUrl.lastIndexOf("/") + 1, jdbcUrl.indexOf("?"));

            String backupCommand = MYSQL_DUMP_PATH + " -u" + dbUsername + " -p" + dbPassword + " " + dbName + " > " + backupFile.getAbsolutePath();

            Process runtimeProcess = Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", backupCommand});
            int processComplete = runtimeProcess.waitFor();

            if (processComplete == 0) {
                System.out.println("백업이 성공적으로 완료되었습니다. 파일 위치: " + backupFile.getAbsolutePath());
            } else {
                System.err.println("백업에 실패했습니다.");
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void saveDailyMetrics() {

        Date today = new Date();

        try {
            // 오늘의 메트릭 데이터를 가져와서 DTO로 구성
            UserMetricsDTO metrics = UserMetricsDTO.builder()
                    .todaySignUpCount(adminService.getTodaySignUpCount())
                    .todayLoginCount(adminService.getTodayLoginCount())
                    .todayVisitCount(adminService.getTodayVisitCount())
                    .todayWithdrawalCount(adminService.getTodayWithdrawalCount())
                    .metricDate(today)
                    .build();

            // DTO를 전달하여 daily metrics 테이블에 저장
            adminService.insertDailyMetrics(metrics);

            // UserMetricsAggregateDTO 생성
            UserMetricsAggregateDTO aggregateMetrics = UserMetricsAggregateDTO.builder()
                    .totalSignUpCount(adminService.getCumulativeSignUpCount())
                    .totalLoginCount(adminService.getCumulativeLoginCount())
                    .totalVisitCount(adminService.getCumulativeVisitCount())
                    .totalWithdrawalCount(adminService.getCumulativeWithdrawalCount())
                    .metricDate(today)
                    .build();

            // DTO를 전달하여 누적 메트릭 테이블에 저장
            adminService.insertUserMetricsAggregate(aggregateMetrics);

            // 메트릭 데이터 저장 완료 로그
            logger.info("메트릭 데이터가 성공적으로 저장되었습니다. 날짜: {}", today);

        } catch (NullPointerException e) {
            // 널 값 예외 처리: 사용자 메트릭 관련 에러
            logger.error("메트릭 데이터를 저장하는 중 null 값이 발견되었습니다: {}", e.getMessage());
            throw new ApplicationException(ApplicationError.USER_METRICS_SAVE_FAILED);

        } catch (ApplicationException e) {
            // 커스텀 예외 처리: 스케줄러 관련 에러
            logger.error("스케줄러 실행 중 오류 발생: {}", e.getMessage());
            throw new ApplicationException(ApplicationError.SCHEDULER_FAILED);

        } catch (Exception e) {
            // 일반 예외 처리: 사용자 메트릭 저장 실패
            logger.error("메트릭 데이터를 저장하는 중 예기치 않은 오류가 발생했습니다: {}", e.getMessage());
            throw new ApplicationException(ApplicationError.USER_METRICS_SAVE_FAILED);
        }
    }
}
