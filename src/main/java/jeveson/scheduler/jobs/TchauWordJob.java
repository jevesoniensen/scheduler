package jeveson.scheduler.jobs;

import jeveson.scheduler.annotation.CronJob;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@DisallowConcurrentExecution
@CronJob("${tchauWordJob.cron:1/4 * * * * ?}")
public class TchauWordJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss.SSS");
        //System.out.println("Tchau Word " + sdf.format(new Date()));
    }
}
