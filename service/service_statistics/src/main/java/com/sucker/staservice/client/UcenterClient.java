package com.sucker.staservice.client;

import com.sucker.commonutils.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient("service-ucenter")
public interface UcenterClient {

    //查询m某一天注册人数
    @GetMapping("educenter/member/countRegister/{day}")
    public R countRegister(@PathVariable("day") String day);

}
