package com.sucker.eduservice.client;

import com.sucker.commonutils.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "service-vod",fallback = VodFileDegradeFeignClient.class)
@Component
public interface VodClient {

    //定义调用的方法的路径
    //@PathVariable 必须指定参数名称，跟上面路径传参一样
    //根据视频id删除阿里云视频
    @DeleteMapping("/eduvod/video/removeAlyVideo/{id}")
    public R removeAlyVideo(@PathVariable("id") String id);


    //删除多个阿里云视频,可以循环调用上面那个方法，但是这里写个另外的
    //参数是多个视频id  使用List
    @DeleteMapping("/eduvod/video/delete-batch")
    public R deleteBatch(@RequestParam("videoIdList") List<String> videoIdList);


}
