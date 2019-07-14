package jeveson.scheduler.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties("scheduler")
public class QuartzProperties {

    private Map<String, String> quartzProperties = new HashMap<>();

    public Map<String, String> getQuartzProperties() {
        return quartzProperties;
    }

    public void setQuartzProperties(Map<String, String> quartzProperties) {
        this.quartzProperties = quartzProperties;
    }
}
