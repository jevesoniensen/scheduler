package jeveson.scheduler.api;

import jeveson.scheduler.dto.OnDemandJob;
import org.quartz.JobDetail;
import org.quartz.ScheduleBuilder;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

@RestController
public class OnDemandJobsController {

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    private JobDetail jobDetail;

    @PostConstruct
    public void init(){

        try {
            jobDetail = getOnDemandJob();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping(value = {"/addJob"})
    public void addJob(@RequestBody OnDemandJob newJob) throws Exception{

        SimpleDateFormat sdfDateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");

        ScheduleBuilder<? extends Trigger> simpleSchedule = SimpleScheduleBuilder.simpleSchedule();
        Trigger trigger = newTrigger().startAt(getDelayedDate(10))
            .forJob(jobDetail).usingJobData("data",newJob.getData())
            .usingJobData("initDate",sdfDateTime.format(new Date()))
            .withIdentity(newJob.getName() + "Trigger", "default").withSchedule(simpleSchedule)
            .build();


        schedulerFactoryBean.getScheduler().scheduleJob(trigger);

    }

    private JobDetail getOnDemandJob() throws Exception{
        JobDetail job = newJob(jeveson.scheduler.jobs.OnDemandJob.class)
            .withIdentity("onDemandJob", "default")
            .storeDurably().build();

        schedulerFactoryBean.getScheduler().addJob(job, false);

        return job;
    }

    public static Date getDelayedDate(int seconds) {
        LocalDateTime localDateTime = LocalDateTime.now().plus(seconds, ChronoUnit.SECONDS);
        return Date.from( localDateTime.atZone( ZoneId.systemDefault()).toInstant());
    }
}
