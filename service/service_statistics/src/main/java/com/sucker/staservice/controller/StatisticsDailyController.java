package com.sucker.staservice.controller;


import com.sucker.commonutils.R;
import com.sucker.staservice.service.StatisticsDailyService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 网站统计日数据 前端控制器
 * </p>
 *
 * @author sucker
 * @since 2022-02-20
 */
@RestController
@RequestMapping("/staservice/sta")
//@CrossOrigin
public class StatisticsDailyController {

    @Autowired
    private StatisticsDailyService staService;

    //统计某一天注册人数,生成统计数据
    @ApiOperation(value = "统计某一天注册人数,生成统计数据")
    @PostMapping("registerCount/{day}")
    public R registerCount(@PathVariable String day){
        staService.registerCount(day);
        return R.ok();
    }


    //图表显示，返回两部分数据，日期Json数组，数量Json数组
    @ApiOperation(value = "图表显示，返回两部分数据，日期Json数组，数量Json数组")
    @GetMapping("showData/{type}/{begin}/{end}")
    public R showData(@PathVariable String type,@PathVariable String begin,@PathVariable String end){
        Map<String ,Object> map = staService.getShowData(type,begin,end);
        return R.ok().data(map);
    }


}

