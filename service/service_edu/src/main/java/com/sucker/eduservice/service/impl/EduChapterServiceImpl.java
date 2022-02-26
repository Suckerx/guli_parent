package com.sucker.eduservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sucker.eduservice.entity.EduChapter;
import com.sucker.eduservice.entity.EduVideo;
import com.sucker.eduservice.entity.chapter.ChapterVo;
import com.sucker.eduservice.entity.chapter.VideoVo;
import com.sucker.eduservice.mapper.EduChapterMapper;
import com.sucker.eduservice.service.EduChapterService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sucker.eduservice.service.EduVideoService;
import com.sucker.servicebase.exceptionhandler.GuliException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author sucker
 * @since 2022-02-08
 */
@Service
public class EduChapterServiceImpl extends ServiceImpl<EduChapterMapper, EduChapter> implements EduChapterService {

    @Autowired
    private EduVideoService eduVideoService;//注入小节service

    //课程大纲列表，根据课程Id进行查询
    @Override
    public List<ChapterVo> getChapterVideoByCourseId(String courseId) {

        //根据课程id查询课程里面所有章节
        QueryWrapper<EduChapter> chapterWrapper = new QueryWrapper<>();
        chapterWrapper.eq("course_id",courseId);
        List<EduChapter> eduChapterList = baseMapper.selectList(chapterWrapper);

        //根据课程id查询课程里面所有小节
        QueryWrapper<EduVideo> videoWrapper = new QueryWrapper<>();
        videoWrapper.eq("course_id",courseId);
        List<EduVideo> eduVideoList = eduVideoService.list(videoWrapper);

        //遍历查询章节list集合进行封装
        List<ChapterVo> finalList = new ArrayList<>();

        //遍历查询的list集合进行封装
        for (int i = 0; i < eduChapterList.size(); i++) {
            EduChapter eduChapter = eduChapterList.get(i);
            ChapterVo chapterVo = new ChapterVo();
            BeanUtils.copyProperties(eduChapter,chapterVo);
            finalList.add(chapterVo);

            //创建集合，用于封装章节的小节
            ArrayList<VideoVo> videoList = new ArrayList<>();

            //遍历查询小节list集合，进行封装
            for (int j = 0; j < eduVideoList.size(); j++) {
                EduVideo eduVideo = eduVideoList.get(j);
                if(eduVideo.getChapterId().equals(eduChapter.getId())){
                    VideoVo videoVo = new VideoVo();
                    BeanUtils.copyProperties(eduVideo,videoVo);
                    videoList.add(videoVo);
                }

            }
            //把封装之后小节list集合，放到章节对象里面
            chapterVo.setChildren(videoList);
        }

        return finalList;
    }

    //删除章节
    @Override
    public boolean deleteChapter(String chapterId) {
        //根据chapterId查询小节，若有数据，就不删除
        QueryWrapper<EduVideo> wrapper = new QueryWrapper<>();
        wrapper.eq("chapter_id",chapterId);
        int count = eduVideoService.count(wrapper);
        if(count > 0)throw new GuliException(20001,"不能删除");//查询出小节不删除
        else {
            int result = baseMapper.deleteById(chapterId);
            return result > 0;
        }

    }

    //根据课程id删除章节
    @Override
    public void removeChapterByCourseId(String courseId) {
        QueryWrapper<EduChapter> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id",courseId);
        baseMapper.delete(wrapper);
    }
}
