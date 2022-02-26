package com.sucker.msmservice.controller;

import com.sucker.commonutils.R;
import com.sucker.msmservice.service.MsmService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(description = "短信服务")
@RestController
//@CrossOrigin
@RequestMapping("/edumsm/msm")
public class MsmController {

    @Autowired
    private MsmService msmService;


    //发送短信
    @ApiOperation(value = "发送短信")
    @PostMapping("send/{phone}")
    public R sendMsm(@PathVariable String phone){
        boolean send = msmService.send(phone);
        if (send) {
            return R.ok();
        }
        return R.error().message("短信发送失败");
    }

}
