package com.sucker.canal;

import com.sucker.canal.client.CanalClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.Resource;

@SpringBootApplication
@ComponentScan({"com.sucker"})
public class CanalApplication implements CommandLineRunner {

    @Resource
    private CanalClient canalClient;

    public static void main(String[] args) {
        SpringApplication.run(CanalApplication.class,args);
    }

    @Override
    public void run(String... args) throws Exception {
        //项目启动时，执行canal客户端监听
        canalClient.run();
    }
}
