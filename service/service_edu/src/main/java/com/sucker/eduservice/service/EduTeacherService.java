package com.sucker.eduservice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sucker.eduservice.entity.EduTeacher;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * <p>
 * 讲师 服务类
 * </p>
 *
 * @author sucker
 * @since 2022-01-27
 */
public interface EduTeacherService extends IService<EduTeacher> {

    //前台分页查询讲师
    Map<String, Object> getTeacherFrontList(Page<EduTeacher> pageTeacher);
}
