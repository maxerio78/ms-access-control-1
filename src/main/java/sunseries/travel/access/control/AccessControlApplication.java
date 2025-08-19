package sunseries.travel.access.control;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class AccessControlApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(AccessControlApplication.class, args);
        while (true) {
            Thread.sleep(Long.MAX_VALUE);
        }
    }
}
