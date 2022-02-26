package com.sucker.eduservice.service;

import com.sucker.eduservice.entity.EduChapter;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sucker.eduservice.entity.chapter.ChapterVo;

import java.util.List;

/**
 * <p>
 * 课程 服务类
 * </p>
 *
 * @author sucker
 * @since 2022-02-08
 */
public interface EduChapterService extends IService<EduChapter> {
    //课程大纲列表，根据课程Id进行查询
    List<ChapterVo> getChapterVideoByCourseId(String courseId);

    //删除章节
    boolean deleteChapter(String chapterId);

    //根据课程id删除章节
    void removeChapterByCourseId(String courseId);
}
