package fingertips.backend.admin.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class SchedulerService {

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
}
