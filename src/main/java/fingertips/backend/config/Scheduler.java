package fingertips.backend.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
public class Scheduler {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(RootConfig.class);

        try {
            Thread.sleep(1000000); // 1000000ms = 1000초
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
