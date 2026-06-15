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
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.LoggerFactory;

public class AuthorizationJobSchedularController implements Job {
    

 

    @SuppressWarnings("unchecked")
    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        System.out.println("Job Started: " + arg0.getJobDetail().getKey().getName() + " [" + new Date() + "]");
        //new MultithreadingJob("NoActionTaken").start();
        new MultithreadingJob("UnderProcess").start();
        //new MultithreadingJob("NeedMoreDetails").start();
        //new MultithreadingJob("Approval").start();
       // System.out.println("Job Finished: " + arg0.getJobDetail().getKey().getName() + " [" + new Date() + "]");
    }

}



class MultithreadingJob extends Thread {
        private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(X12Client.class);

    String categoryType;

    public MultithreadingJob(String categoryType) {
        this.categoryType = categoryType;
    }

    @Override
    public void run() {
        try {
           
              //new WasselEligibility().submitEligibility();
          new  MainTestClient().sendMessage();
         
     } catch (Exception e) {
         LOGGER.error("Error Catched:----------------------"+e.getMessage());
        }
    }
}
