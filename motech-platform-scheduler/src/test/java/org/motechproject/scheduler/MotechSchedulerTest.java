/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2011 Grameen Foundation USA.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Grameen Foundation USA, nor its respective contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA AND ITS CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION USA OR ITS CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package org.motechproject.scheduler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.RepeatingSchedulableJob;
import org.motechproject.model.RunOnceSchedulableJob;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
// specifies the Spring configuration to load for this test fixture
@ContextConfiguration(locations={"/testApplicationContext.xml"})

public class MotechSchedulerTest {

    @Autowired
    private MotechSchedulerService motechScheduler;

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    private String uuidStr = UUID.randomUUID().toString();

    @Test
    public void scheduleTest() throws Exception{

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("JobID", uuidStr);
        MotechEvent motechEvent = new MotechEvent("testEvent", params);
        CronSchedulableJob cronSchedulableJob = new CronSchedulableJob(motechEvent, "0 0 12 * * ?");

        int scheduledJobsNum = schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length;

        motechScheduler.scheduleJob(cronSchedulableJob);

        assertEquals(scheduledJobsNum+1, schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length);
    }

    @Test
    public void scheduleExistingJobTest() throws Exception{

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("JobID", uuidStr);
        MotechEvent motechEvent = new MotechEvent("testEvent", params);
        CronSchedulableJob cronSchedulableJob = new CronSchedulableJob(motechEvent, "0 0 12 * * ?");


        Map<String, Object> newParameters = new HashMap<String, Object>();
        newParameters.put("param1", "value1");
        newParameters.put("JobID", uuidStr);

        String newCronExpression = "0 0 0 * * ?";

        MotechEvent newMotechEvent = new MotechEvent("testEvent", newParameters);
        CronSchedulableJob newCronSchedulableJob = new CronSchedulableJob(newMotechEvent, newCronExpression);

        int scheduledJobsNum = schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length;

        motechScheduler.scheduleJob(cronSchedulableJob);
        assertEquals(scheduledJobsNum+1, schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length);


        motechScheduler.scheduleJob(newCronSchedulableJob);

        assertEquals(scheduledJobsNum+1, schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length);

        CronTrigger trigger =  (CronTrigger) schedulerFactoryBean.getScheduler().getTrigger(uuidStr, MotechSchedulerServiceImpl.JOB_GROUP_NAME);
        JobDetail jobDetail =  schedulerFactoryBean.getScheduler().getJobDetail(uuidStr, MotechSchedulerServiceImpl.JOB_GROUP_NAME);
        JobDataMap jobDataMap = jobDetail.getJobDataMap();

        assertEquals(newCronExpression, trigger.getCronExpression());
        assertEquals(3, jobDataMap.size());
    }


    @Test(expected = MotechSchedulerException.class)
    public void scheduleInvalidCronExprTest() throws Exception{

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("JobID", uuidStr);
        MotechEvent motechEvent = new MotechEvent("testEvent", params);
        CronSchedulableJob cronSchedulableJob = new CronSchedulableJob(motechEvent, " ?");

        motechScheduler.scheduleJob(cronSchedulableJob);

    }

    @Test(expected = IllegalArgumentException.class)
    public void scheduleNullJobTest() throws Exception{
        motechScheduler.scheduleJob(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void scheduleNullRunOnceJobTest() throws Exception{
        motechScheduler.scheduleRunOnceJob(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void scheduleNullRepeatingJobTest() throws Exception{
        motechScheduler.scheduleRepeatingJob(null);
    }

    @Test
    public void updateScheduledJobHappyPathTest() throws Exception {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("JobID", uuidStr);
        MotechEvent motechEvent = new MotechEvent("testEvent", params);
        CronSchedulableJob cronSchedulableJob = new CronSchedulableJob(motechEvent, "0 0 12 * * ?");

        motechScheduler.scheduleJob(cronSchedulableJob);

        String patientIdKeyName = "patientId";
        String patientId = "1";
        params = new HashMap<String, Object>();
        params.put(patientIdKeyName, patientId);
        params.put("JobID", uuidStr);

        motechEvent = new MotechEvent("testEvent", params);

        motechScheduler.updateScheduledJob(motechEvent);

        JobDataMap jobDataMap = schedulerFactoryBean.getScheduler().getJobDetail(uuidStr, MotechSchedulerServiceImpl.JOB_GROUP_NAME).getJobDataMap();

        assertEquals(patientId, jobDataMap.getString(patientIdKeyName));
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateScheduledJobNullTest() throws Exception {

        motechScheduler.updateScheduledJob(null);
    }


    @Test
    public void rescheduleJobHappyPathTest() throws Exception{

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("JobID", uuidStr);
        MotechEvent motechEvent = new MotechEvent("testEvent", params);
        CronSchedulableJob cronSchedulableJob = new CronSchedulableJob(motechEvent, "0 0 12 * * ?");

        motechScheduler.scheduleJob(cronSchedulableJob);

        int scheduledJobsNum = schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length;

        String newCronExpression = "0 0 10 * * ?";

        motechScheduler.rescheduleJob(uuidStr, newCronExpression);
        assertEquals(scheduledJobsNum, schedulerFactoryBean.getScheduler().getTriggerNames( MotechSchedulerServiceImpl.JOB_GROUP_NAME).length);

        CronTrigger trigger = (CronTrigger) schedulerFactoryBean.getScheduler().getTrigger(uuidStr, MotechSchedulerServiceImpl.JOB_GROUP_NAME);
        String triggerCronExpression = trigger.getCronExpression();

        assertEquals(newCronExpression, triggerCronExpression);
    }


    @Test(expected = IllegalArgumentException.class)
    public void rescheduleJobNullJobIdTest() {
        motechScheduler.rescheduleJob(null, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void rescheduleJobNullCronExpressionTest() {
        motechScheduler.rescheduleJob("", null);
    }

    @Test(expected = MotechSchedulerException.class)
    public void rescheduleJobInvalidCronExpressionTest() {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("JobID", uuidStr);
        MotechEvent motechEvent = new MotechEvent("testEvent", params);
        CronSchedulableJob cronSchedulableJob = new CronSchedulableJob(motechEvent, "0 0 12 * * ?");

        motechScheduler.scheduleJob(cronSchedulableJob);

        motechScheduler.rescheduleJob(uuidStr, "?");
    }

    @Test(expected = MotechSchedulerException.class)
    public void rescheduleNotExistingJobTest() {
        motechScheduler.rescheduleJob("0", "?");
    }


    @Test
    public void scheduleRunOnceJobTest() throws Exception{

        String uuidStr = UUID.randomUUID().toString();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("JobID", uuidStr);
        MotechEvent motechEvent = new MotechEvent("TestEvent", params);
        RunOnceSchedulableJob schedulableJob = new RunOnceSchedulableJob(motechEvent, new Date((new Date()).getTime()+5000));

        int scheduledJobsNum = schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length;

        motechScheduler.scheduleRunOnceJob(schedulableJob);

        assertEquals(scheduledJobsNum+1, schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void scheduleRunOncePastJobTest() throws Exception{

        Calendar calendar =  Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);

        MotechEvent motechEvent = new MotechEvent(null, null);

        RunOnceSchedulableJob schedulableJob = new RunOnceSchedulableJob(motechEvent, calendar.getTime());

        int scheduledJobsNum = schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length;

        motechScheduler.scheduleRunOnceJob(schedulableJob);
    }

    @Test
    public void testScheduleRepeatingJob() throws Exception{
        String uuidStr = UUID.randomUUID().toString();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("JobID", uuidStr);
        MotechEvent motechEvent = new MotechEvent("TestEvent", params);

        Calendar cal = Calendar.getInstance();
        Date start = cal.getTime();
        cal.add(Calendar.DATE, 1);
        Date end = cal.getTime();

        RepeatingSchedulableJob schedulableJob = new RepeatingSchedulableJob(motechEvent, start, end, 5, 5000);

        int scheduledJobsNum = schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length;

        motechScheduler.scheduleRepeatingJob(schedulableJob);

        assertEquals(scheduledJobsNum+1, schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testScheduleRepeatingJobTest_NoStartDate() throws Exception{
        String uuidStr = UUID.randomUUID().toString();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("JobID", uuidStr);
        MotechEvent motechEvent = new MotechEvent("TestEvent", params);
        RepeatingSchedulableJob schedulableJob = new RepeatingSchedulableJob(motechEvent, null, new Date(), 5, 5000);

        int scheduledJobsNum = schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length;

        motechScheduler.scheduleRepeatingJob(schedulableJob);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testScheduleRepeatingJobTest_NoEndDate() throws Exception{
        String uuidStr = UUID.randomUUID().toString();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("JobID", uuidStr);
        MotechEvent motechEvent = new MotechEvent("TestEvent", params);
        RepeatingSchedulableJob schedulableJob = new RepeatingSchedulableJob(motechEvent, new Date(), null, 5, 5000);

        int scheduledJobsNum = schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length;

        motechScheduler.scheduleRepeatingJob(schedulableJob);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testScheduleRepeatingJobTest_NullJob() throws Exception{
        String uuidStr = UUID.randomUUID().toString();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("JobID", uuidStr);
        MotechEvent motechEvent = new MotechEvent("TestEvent", params);
        RepeatingSchedulableJob schedulableJob = null;

        int scheduledJobsNum = schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length;

        motechScheduler.scheduleRepeatingJob(schedulableJob);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testScheduleRepeatingJobTest_NullEvent() throws Exception{
        String uuidStr = UUID.randomUUID().toString();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("JobID", uuidStr);
        MotechEvent motechEvent = null;
        RepeatingSchedulableJob schedulableJob = new RepeatingSchedulableJob(motechEvent, new Date(), new Date(), 5, 5000);;

        int scheduledJobsNum = schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length;

        motechScheduler.scheduleRepeatingJob(schedulableJob);
    }

    @Test
    public void unscheduleJobTest() throws Exception{

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("JobID", uuidStr);
        MotechEvent motechEvent = new MotechEvent("testEvent", params);
        CronSchedulableJob cronSchedulableJob = new CronSchedulableJob(motechEvent, "0 0 12 * * ?");

        motechScheduler.scheduleJob(cronSchedulableJob);
        int scheduledJobsNum = schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length;

        motechScheduler.unscheduleJob(uuidStr);

        assertEquals(scheduledJobsNum - 1, schedulerFactoryBean.getScheduler().getTriggerNames(MotechSchedulerServiceImpl.JOB_GROUP_NAME).length);
    }

}
