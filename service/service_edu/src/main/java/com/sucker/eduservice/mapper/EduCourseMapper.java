package com.sucker.eduservice.mapper;

import com.sucker.eduservice.entity.EduCourse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sucker.eduservice.entity.frontvo.CourseWebVo;
import com.sucker.eduservice.entity.vo.CoursePublishVo;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 课程 Mapper 接口
 * </p>
 *
 * @author sucker
 * @since 2022-02-08
 */
@Mapper
public interface EduCourseMapper extends BaseMapper<EduCourse> {

    //根据课程id查询课程确认信息
    public CoursePublishVo getPublishCourseInfo(String courseId);

    //根据课程id，编写sql语句查询课程信息
    CourseWebVo getBaseCourseInfo(String courseId);
}
