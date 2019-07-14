package jeveson.scheduler.config;

import jeveson.scheduler.Utils.CustomSpringBeanJobFactory;
import jeveson.scheduler.annotation.CronJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

@Configuration
public class QuartzConfig {

    @Autowired
    DataSource ds;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private QuartzProperties quartzProperties;

    @Autowired
    private ConfigurableBeanFactory configurableBeanFactory;

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {

        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setQuartzProperties(getQuartzConfigProperties());
        schedulerFactoryBean.setJobFactory(getCustomSpringBeanJobFactory());
        schedulerFactoryBean.setDataSource( ds );
        schedulerFactoryBean.setSchedulerFactoryClass(StdSchedulerFactory.class);
        schedulerFactoryBean.setOverwriteExistingJobs(true);

        scheduleJobs(schedulerFactoryBean);

        return schedulerFactoryBean;
    }

    public void scheduleJobs(SchedulerFactoryBean schedulerFactoryBean){

        Set<Map.Entry<String, Job>> entries = getCronJobAnnotatedJobs().entrySet();
        List<ScheduleJobs> scheduleJobsList =
                entries.stream().map(entry -> processAnnotation(schedulerFactoryBean, entry))
                        .collect(Collectors.toList());


        List<JobDetail> jobDetails = scheduleJobsList.stream().map(s -> s.jobDetail).collect(Collectors.toList());
        List<CronTrigger> triggers = scheduleJobsList.stream().map(s -> s.trigger).collect(Collectors.toList());
        schedulerFactoryBean.setJobDetails((JobDetail[])jobDetails.toArray(new JobDetail[0]));
        schedulerFactoryBean.setTriggers((Trigger[])triggers.toArray(new Trigger[0]));
    }

    private ScheduleJobs processAnnotation(SchedulerFactoryBean schedulerFactoryBean, Map.Entry<String, Job> jobEntry) {

        Job entryValue = jobEntry.getValue();
        String beanName = entryValue.getClass().getName();
        CronJob cron = applicationContext.findAnnotationOnBean(jobEntry.getKey(), CronJob.class);

        String cronValue = configurableBeanFactory.resolveEmbeddedValue(cron.value());

        JobDetail jobDetail = newJob(entryValue.getClass())
                .withIdentity(beanName, cron.group())
                .storeDurably().build();

        CronTrigger trigger = newTrigger()
                .startNow().forJob(jobDetail)
                .withIdentity(beanName + "Trigger", cron.group())
                .withSchedule(CronScheduleBuilder.cronSchedule(cronValue))
                .build();

        ScheduleJobs scheduleJobs = new ScheduleJobs();
        scheduleJobs.jobDetail = jobDetail;
        scheduleJobs.trigger = trigger;

        return scheduleJobs;
    }

    private Map<String, Job> getCronJobAnnotatedJobs() {
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(CronJob.class);

        if( beansWithAnnotation == null ) {
            return new HashMap<>();
        }

        return beansWithAnnotation
                .entrySet()
                .stream()
                .collect(Collectors.toMap(p -> p.getKey(), p -> (Job)p.getValue()));
    }

    private Properties getQuartzConfigProperties() {
        Properties properties = new Properties();
        properties.putAll(quartzProperties.getQuartzProperties());
        return properties;
    }

    @Bean
    public CustomSpringBeanJobFactory getCustomSpringBeanJobFactory() {
        CustomSpringBeanJobFactory beanJobFactory = new CustomSpringBeanJobFactory();
        beanJobFactory.setApplicationContext(applicationContext);
        return beanJobFactory;
    }

    public static class ScheduleJobs {

        JobDetail jobDetail;
        CronTrigger trigger;
    }
}
