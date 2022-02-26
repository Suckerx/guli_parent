package com.sucker.eduservice.controller;


import com.sucker.commonutils.R;
import com.sucker.eduservice.client.VodClient;
import com.sucker.eduservice.entity.EduVideo;
import com.sucker.eduservice.service.EduVideoService;
import com.sucker.servicebase.exceptionhandler.GuliException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 课程视频 前端控制器
 * </p>
 *
 * @author sucker
 * @since 2022-02-08
 */
@Api(description = "课程小节管理")
@RestController
@RequestMapping("/eduservice/video")
//@CrossOrigin
public class EduVideoController {

    @Autowired
    private EduVideoService eduVideoService;

    //注入VodClient
    @Autowired
    private VodClient vodClient;

    //添加小节
    @ApiOperation(value = "添加小节")
    @PostMapping("addVideo")
    public R addVideo(@RequestBody EduVideo eduVideo){
        eduVideoService.save(eduVideo);
        return R.ok();
    }

    //删除小节
    @ApiOperation(value = "删除小节")
    @DeleteMapping("{id}")
    public R deleteVideo(@PathVariable String id){
        //根据小节id获取视频id，调用方法删除视频
        EduVideo eduVideo = eduVideoService.getById(id);
        String videoSourceId = eduVideo.getVideoSourceId();
        //判断小节里面是否有视频id
        if(!ObjectUtils.isEmpty(videoSourceId)){
            //根据视频id，远程调用实现视频删除
            R result = vodClient.removeAlyVideo(videoSourceId);
            if(result.getCode() == 20001) throw new GuliException(20001,"删除视频失败，熔断器...");
        }
        //删除小节
        eduVideoService.removeById(id);
        return R.ok();
    }

    //修改小节
    @ApiOperation(value = "修改小节")
    @PostMapping("updateVideo")
    public R updateVideo(@RequestBody EduVideo eduVideo){
        eduVideoService.updateById(eduVideo);
        return R.ok();
    }

}

