package com.sucker.eduservice.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sucker.eduservice.entity.EduSubject;
import com.sucker.eduservice.entity.excel.SubjectData;
import com.sucker.eduservice.entity.subject.OneSubject;
import com.sucker.eduservice.entity.subject.TwoSubject;
import com.sucker.eduservice.listener.SubjectExcelListener;
import com.sucker.eduservice.mapper.EduSubjectMapper;
import com.sucker.eduservice.service.EduSubjectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.ibatis.annotations.One;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.lang.model.type.ArrayType;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程科目 服务实现类
 * </p>
 *
 * @author sucker
 * @since 2022-02-06
 */
@Service
public class EduSubjectServiceImpl extends ServiceImpl<EduSubjectMapper, EduSubject> implements EduSubjectService {

    //添加课程分类
    @Override
    public void saveSubject(MultipartFile file,EduSubjectService subjectService) {
        try{
            //文件输入流
            InputStream in = file.getInputStream();
            //调用方法进行读取
            EasyExcel.read(in, SubjectData.class,new SubjectExcelListener(subjectService)).sheet().doRead();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //课程分类列表树形
    @Override
    public List<OneSubject> getAllOneTwoSubject() {

        //查询所有一级分类
        QueryWrapper<EduSubject> wrapperOne = new QueryWrapper<>();
        wrapperOne.eq("parent_id",0);//添加条件，等于
        List<EduSubject> oneSubjectList = baseMapper.selectList(wrapperOne);

        //查询所有二级分类
        QueryWrapper<EduSubject> wrapperTwo = new QueryWrapper<>();
        wrapperTwo.ne("parent_id",0);//添加条件，等于
        List<EduSubject> twoSubjectList = baseMapper.selectList(wrapperTwo);

        //创建list集合，用于封装最终数据
        List<OneSubject> finalSubjectList = new ArrayList<>();

        //封装一级分类
        //查询出来的所有一级分类list集合遍历，得到每个分类对象，获取每个分类的值
        //封装到要求的list集合 List<OneSubject> finalSubjectList
        for (int i = 0; i < oneSubjectList.size(); i++) {//遍历oneSubjectList
            //得到oneSubject每个eduSubject对象
            EduSubject eduSubject = oneSubjectList.get(i);
            //普通方法是把eduSubject中的值取出来放到oneSubject对象中再把多个该对象加入集合
            OneSubject oneSubject = new OneSubject();
            //把eduSubject中的值复制到oneSubject
            BeanUtils.copyProperties(eduSubject,oneSubject);//简化版工具类实现，也是使用getset方法
            finalSubjectList.add(oneSubject);

            //封装二级分类
            //在一级分类循环遍历查询二级分类
            //创建list集合封装每一个一级分类的二级分类
            List<TwoSubject> twoFinalSubjectList = new ArrayList<>();
            for (int j = 0; j < twoSubjectList.size(); j++) {
                EduSubject tSubject = twoSubjectList.get(j);
                if(tSubject.getParentId().equals(eduSubject.getId())){
                    TwoSubject twoSubject = new TwoSubject();
                    BeanUtils.copyProperties(tSubject,twoSubject);
                    twoFinalSubjectList.add(twoSubject);
                }
            }

            //把一级下面所有二级分类封装到一级分类中
            oneSubject.setChildren(twoFinalSubjectList);
        }

        return finalSubjectList;
    }

}
