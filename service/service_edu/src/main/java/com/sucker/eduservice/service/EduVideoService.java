package com.sucker.eduservice.service;

import com.sucker.eduservice.entity.EduVideo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 课程视频 服务类
 * </p>
 *
 * @author sucker
 * @since 2022-02-08
 */
public interface EduVideoService extends IService<EduVideo> {

    //根据课程id删除小节
    void removeVideoByCourseId(String courseId);
}
