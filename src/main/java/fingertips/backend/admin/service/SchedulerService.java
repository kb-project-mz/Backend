package fingertips.backend.admin.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SchedulerService {

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정 00:00에 실행
    public void performTask() {
        System.out.println("Scheduled Task executed at: " + System.currentTimeMillis());
    }
}
