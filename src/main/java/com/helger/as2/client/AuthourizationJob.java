/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.helger.as2.client;

/**
 *
 * @author Shussien
 */


import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class AuthourizationJob {

    private static AuthourizationJob instance;

    public void task(Date date) throws SchedulerException, IOException, InterruptedException {
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();
        JobDetail authJobDetail = JobBuilder.newJob(AuthorizationJobSchedularController.class).withIdentity("Send X12 Mesage").storeDurably().build();
       CronTrigger authCronTrigger = TriggerBuilder.newTrigger().withIdentity("Send X12 Mesage").withSchedule(CronScheduleBuilder.cronSchedule("0/2 * * ? * *")).forJob(authJobDetail).build();
        scheduler.scheduleJob(authJobDetail, authCronTrigger);
      
        scheduler.start();

      
    }

    public static AuthourizationJob getInstance() {
        if (instance == null) {
            synchronized (AuthourizationJob.class) {
                instance = new AuthourizationJob();
            }
        }
        return instance;
    }
}
