package com.sucker.staservice.schedule;

import com.sucker.staservice.service.StatisticsDailyService;
import com.sucker.staservice.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ScheduledTask {

    @Autowired
    private StatisticsDailyService staService;

    @Scheduled(cron = "0/5 * * * * ?")//每隔5秒执行一次这个方法,只支持6位参数
    public void task1(){
        System.out.println("**************task1执行了");
    }

    //每天凌晨一点，执行方法，把数据查询进行添加
    @Scheduled(cron = "0 0 1 * * ?")
    public void task2(){
        //获取前一天日期
        staService.registerCount(DateUtil.formatDate(DateUtil.addDays(new Date(),-1)));
    }


}
