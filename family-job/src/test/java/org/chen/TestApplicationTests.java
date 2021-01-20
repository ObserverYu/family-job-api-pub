package org.chen;

import lombok.extern.slf4j.Slf4j;
import org.chen.manager.JobManager;
import org.chen.service.IStepRecordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {FamilyJobApplication.class})
@ActiveProfiles("dev")
@ComponentScan("org.chen")
@Slf4j
class TestApplicationTests {

    @Autowired
    private IStepRecordService stepRecordService;

    @Autowired
    private JobManager jobManager;

//    @Test
//    public void testTrans(){
//        JobUser jobUser = new JobUser();
//        jobUser.setCostStep(121L);
//        jobManager.checkAndUpdateStep(jobUser,"","",1L);
//
//    }


    @Test
    public void testInsertBySharding(){

        for (int i = 0; i < 100000; i++) {

        }
    }

//    @Autowired
//    private LinkUpAutoMarkTimer linkUpAutoMarkTimer;
//    @Test
//    public void testWecomMark() throws InterruptedException {
//        linkUpAutoMarkTimer.preMsg();
//        TimeUnit.SECONDS.sleep(20);
//    }

}
