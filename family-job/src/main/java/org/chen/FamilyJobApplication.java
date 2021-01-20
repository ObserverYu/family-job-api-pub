package org.chen;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.chen.listener.ApplicationStartedEventListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Set;

//使用多数据源配置  要去掉该类 阻止druid自动配置数据源
@SpringBootApplication(exclude = DruidDataSourceAutoConfigure.class)
@EnableScheduling
@EnableAsync
@Slf4j
@ComponentScan("org.chen")
public class FamilyJobApplication {
    private String bootstrap;

    public String getBootstrap() {
        return bootstrap;
    }

    public void setBootstrap(String bootstrap) {
        this.bootstrap = bootstrap;
    }
    public static void main(String[] args) {
        //		SpringApplication.run(ShardingTestApplication.class, args);
        // 更换启动方式使log4j2.xml能够获取到springboot的配置参数
        SpringApplication app = new SpringApplication(FamilyJobApplication.class);
        Set<ApplicationListener<?>> ls = app.getListeners();
        ApplicationStartedEventListener asel = new ApplicationStartedEventListener();
        app.addListeners(asel);
        app.run(args);
        log.debug("====================<<<<< debug level open !!  >>>>> ============================");
        log.info("====================<<<<< info level open  !!>>>>> ============================");
    }

}
