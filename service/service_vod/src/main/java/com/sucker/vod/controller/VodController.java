package com.sucker.vod.controller;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthRequest;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthResponse;
import com.sucker.commonutils.R;
import com.sucker.servicebase.exceptionhandler.GuliException;
import com.sucker.vod.service.VodService;
import com.sucker.vod.utils.ConstantVodUtils;
import com.sucker.vod.utils.InitVodClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Api(description = "视频管理")
@RestController
@RequestMapping("/eduvod/video")
//@CrossOrigin
public class VodController {

    @Autowired
    private VodService vodService;

    @ApiOperation(value = "上传视频到阿里云")
    @PostMapping("uploadAliyVideo")
    public R uploadAliyVideo(MultipartFile file){
        String vidoeId = vodService.uploadVideoAly(file);
        return R.ok().data("videoId",vidoeId);
    }

    //根据视频id删除阿里云视频
    @ApiOperation("根据视频id删除阿里云视频")
    @DeleteMapping("removeAlyVideo/{id}")
    public R removeAlyVideo(@PathVariable String id){
        vodService.removeVideo(id);
        return R.ok().message("视频删除成功");
    }

    //删除多个阿里云视频,可以循环调用上面那个方法，但是这里写个另外的
    //参数是多个视频id  使用List
    @ApiOperation(value = "删除多个阿里云视频")
    @DeleteMapping("delete-batch")
    public R deleteBatch(@RequestParam("videoIdList") List<String> videoIdList){
        vodService.removeMoreAlyVideo(videoIdList);
        return R.ok();
    }


    //根据视频id获取视频凭证
    @ApiOperation(value = "根据视频id获取视频凭证")
    @GetMapping("getPlayAuth/{id}")
    public R getPlayAuth(@PathVariable String id){
        try {
            //创建初始化对象
            DefaultAcsClient client = InitVodClient.initVodClient(ConstantVodUtils.ACCESS_KEY_ID, ConstantVodUtils.ACCESS_KEY_SECRET);
            //创建获取视频凭证request和response
            GetVideoPlayAuthRequest request = new GetVideoPlayAuthRequest();
            GetVideoPlayAuthResponse response = new GetVideoPlayAuthResponse();

            //向request设置视频id
            request.setVideoId(id);
            //调用初始化对象方法得到凭证
            response = client.getAcsResponse(request);
            String playAuth = response.getPlayAuth();
            return R.ok().data("playAuth",playAuth);
        } catch (ClientException e) {
            e.printStackTrace();
            throw new GuliException(20001,"获取凭证失败");
        }
    }


}
