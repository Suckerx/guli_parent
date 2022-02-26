package com.sucker.eduservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sucker.eduservice.client.VodClient;
import com.sucker.eduservice.entity.EduVideo;
import com.sucker.eduservice.mapper.EduVideoMapper;
import com.sucker.eduservice.service.EduVideoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程视频 服务实现类
 * </p>
 *
 * @author sucker
 * @since 2022-02-08
 */
@Service
public class EduVideoServiceImpl extends ServiceImpl<EduVideoMapper, EduVideo> implements EduVideoService {

    //注入远程调用vodClient
    @Autowired
    private VodClient vodClient;

    //根据课程id删除小节
    @Override
    public void removeVideoByCourseId(String courseId) {
        //根据课程id查询出所有视频id
        QueryWrapper<EduVideo> wrapperVideo = new QueryWrapper<>();
        wrapperVideo.eq("course_id",courseId);
        wrapperVideo.select("video_source_id");//表示只查视频id，不要其他值
        List<EduVideo> eduVideoList = baseMapper.selectList(wrapperVideo);

        //List<EduVideo>变为List<String>
        List<String> videoIds = new ArrayList<>();
        for (int i = 0; i < eduVideoList.size(); i++) {
            EduVideo eduVideo = eduVideoList.get(i);
            String videoSourceId = eduVideo.getVideoSourceId();
            if(!ObjectUtils.isEmpty(videoSourceId)) videoIds.add(videoSourceId);
        }
        //根据多个视频id删除多个视频
        if(videoIds.size()>0) vodClient.deleteBatch(videoIds);

        QueryWrapper<EduVideo> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id",courseId);
        baseMapper.delete(wrapper);
    }
}
