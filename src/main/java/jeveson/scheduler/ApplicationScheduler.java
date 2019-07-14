package jeveson.scheduler;

import jeveson.scheduler.config.QuartzProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(QuartzProperties.class)
public class ApplicationScheduler {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationScheduler.class, args);
    }
}
