package fingertips.backend.admin.service;

import fingertips.backend.admin.dto.UserMetricsAggregateDTO;
import fingertips.backend.admin.dto.UserMetricsDTO;
import fingertips.backend.admin.mapper.AdminMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class SchedulerService {

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

    @Scheduled(cron = "0 0 * * * ?")
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

    @Scheduled(cron = "0 0 * * * ?")
    public void saveDailyMetrics() {
        try {
            // 오늘의 메트릭 데이터를 가져와서 DTO로 구성
            UserMetricsDTO metrics = UserMetricsDTO.builder()
                    .todaySignUpCount(adminService.getTodaySignUpCount())
                    .todayLoginCount(adminService.getTodayLoginCount())
                    .todayVisitCount(adminService.getTodayVisitCount())
                    .todayWithdrawalCount(adminService.getTodayWithdrawalCount())
//                    .todayTestLinkVisitCount(adminService.getTodayTestLinkVisitCount())
//                    .todayTestResultClickCount(adminService.getTodayTestResultClickCount())
//                    .todayTestSignUpCount(adminService.getTodayTestSignUpCount())
                    .build();

            // DTO를 전달하여 데이터베이스에 저장
            adminService.insertDailyMetrics(metrics);

            System.out.println("메트릭 데이터가 성공적으로 저장되었습니다.");

        } catch (Exception e) {
            System.err.println("메트릭 데이터를 저장하는 데 실패했습니다.");
            e.printStackTrace();
        }
    }
}
