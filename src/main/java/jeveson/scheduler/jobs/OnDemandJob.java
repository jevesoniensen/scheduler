package jeveson.scheduler.jobs;

import jeveson.scheduler.annotation.CronJob;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@DisallowConcurrentExecution
public class OnDemandJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {


        JobDataMap dataMap = jobExecutionContext.getTrigger().getJobDataMap();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss.SSS");
        System.out.println("OnDemand " + sdf.format(new Date()) + " data: " + dataMap.getString("data")
        + " initDate: " + dataMap.getString("initDate"));
    }
}
