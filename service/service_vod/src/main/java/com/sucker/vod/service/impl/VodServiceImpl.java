package com.sucker.vod.service.impl;

import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyun.vod.upload.req.UploadStreamRequest;
import com.aliyun.vod.upload.resp.UploadStreamResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.vod.model.v20170321.DeleteVideoRequest;
import com.aliyuncs.vod.model.v20170321.DeleteVideoResponse;
import com.sucker.servicebase.exceptionhandler.GuliException;
import com.sucker.vod.service.VodService;
import com.sucker.vod.utils.ConstantVodUtils;
import com.sucker.vod.utils.InitVodClient;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class VodServiceImpl implements VodService {

    //上传视频到阿里云
    @Override
    public String uploadVideoAly(MultipartFile file) {
        try {
            //accessKeyId accessKeySecret
            //fileName：上传文件原始名称
            String fileName = file.getOriginalFilename();

            //title:上传后显示的名称
            String title = fileName.substring(0,fileName.lastIndexOf("."));

            //inputStream：上传文件输入流
            InputStream inputStream = file.getInputStream();

            UploadStreamRequest request = new UploadStreamRequest(ConstantVodUtils.ACCESS_KEY_ID, ConstantVodUtils.ACCESS_KEY_SECRET,
                    title, fileName, inputStream);

            UploadVideoImpl uploader = new UploadVideoImpl();
            UploadStreamResponse response = uploader.uploadStream(request);
            String videoId = null;
            if (response.isSuccess()) {
                videoId = response.getVideoId();
            } else { //如果设置回调URL无效，不影响视频上传，可以返回VideoId同时会返回错误码。其他情况上传失败时，VideoId为空，此时需要根据返回错误码分析具体错误原因
                videoId = response.getVideoId();
                System.out.print("VideoId=" + response.getVideoId() + "\n");
                System.out.print("ErrorCode=" + response.getCode() + "\n");
                System.out.print("ErrorMessage=" + response.getMessage() + "\n");
            }
            return videoId;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //根据视频id删除阿里云视频
    @Override
    public void removeVideo(String id) {
        try{
            DefaultAcsClient client = InitVodClient.initVodClient(ConstantVodUtils.ACCESS_KEY_ID, ConstantVodUtils.ACCESS_KEY_SECRET);
            DeleteVideoRequest request = new DeleteVideoRequest();
            //可以设置多个,用逗号隔开
            request.setVideoIds(id);
            DeleteVideoResponse response = client.getAcsResponse(request);
            System.out.print("RequestId = " + response.getRequestId() + "\n");
        }catch (Exception e){
            e.printStackTrace();
            throw new GuliException(20001, "视频删除失败");
        }
    }


    //删除多个阿里云视频
    @Override
    public void removeMoreAlyVideo(List<String> videoIdList) {
        try{
            DefaultAcsClient client = InitVodClient.initVodClient(ConstantVodUtils.ACCESS_KEY_ID, ConstantVodUtils.ACCESS_KEY_SECRET);
            DeleteVideoRequest request = new DeleteVideoRequest();

            //把videoIdList值转换为1，2，3形式传入  注意包是apache.common.lang
            String videoIds = StringUtils.join(videoIdList.toArray(), ",");

            //可以设置多个,用逗号隔开
            request.setVideoIds(videoIds);
            DeleteVideoResponse response = client.getAcsResponse(request);
            System.out.print("RequestId = " + response.getRequestId() + "\n");
        }catch (Exception e){
            e.printStackTrace();
            throw new GuliException(20001, "视频删除失败");
        }
    }


}
