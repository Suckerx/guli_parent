package com.sucker.eduservice.controller;


import com.sucker.commonutils.R;
import com.sucker.eduservice.entity.subject.OneSubject;
import com.sucker.eduservice.service.EduSubjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 课程科目 前端控制器
 * </p>
 *
 * @author sucker
 * @since 2022-02-06
 */
@Api(description = "课程科目")
@RestController
@RequestMapping("/eduservice/subject")
//@CrossOrigin
public class EduSubjectController {

    @Autowired
    private EduSubjectService subjectService;

    //添加课程分类
    //获取上传过来的文件，把内容读取出来
    @ApiOperation(value = "添加课程分类")
    @PostMapping("addSubject")
    public R addSubject(MultipartFile file){
        //上传过来的文件
        subjectService.saveSubject(file,subjectService);
        return R.ok();
    }

    //课程分类列表（树形）
    @ApiOperation(value = "课程分类列表")
    @GetMapping("getAllSubject")
    public R getAllSubject(){
        //list集合中泛型是一级分类
        List<OneSubject> list = subjectService.getAllOneTwoSubject();
        return R.ok().data("list",list);
    }

}

