package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengError;
import com.xuecheng.content.mapper.*;
import com.xuecheng.content.model.dto.ModifyCourseDto;
import com.xuecheng.content.model.dto.QueryCourseMarketDto;
import com.xuecheng.content.model.po.*;
import com.xuecheng.content.service.ModifyCourseService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ModifyCourseServiceImpl implements ModifyCourseService {
    @Autowired
    CourseBaseMapper courseBaseMapper;
    @Autowired
    CourseMarketMapper courseMarketMapper;
    @Autowired
    CourseCategoryMapper courseCategoryMapper;
    @Autowired
    TeachplanMapper teachplanMapper;
    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;
    @Autowired
    CourseTeacherMapper courseTeacherMapper;
    @Override
    public QueryCourseMarketDto updatecourse(long companyId,ModifyCourseDto dto) {
        long id = dto.getId();
        CourseBase courseBase =courseBaseMapper.selectById(id);
        if(courseBase==null){
            XueChengError.cast("课程不存在");
        }
        if(!courseBase.getCompanyId().equals(companyId)){
            XueChengError.cast("只可以修改本公司的课程");
        }
        BeanUtils.copyProperties(dto,courseBase);
        courseBase.setChangeDate(LocalDateTime.now());
        courseBaseMapper.updateById(courseBase);
        CourseMarket marketDto = new CourseMarket();
        BeanUtils.copyProperties(dto,marketDto);
        marketDto.setId(id);
        saveCourseMarket(marketDto);
        return getCourseMarket(id);
    }
    private int saveCourseMarket(CourseMarket courseMarket) {
        if(StringUtils.isBlank(courseMarket.getCharge())){
            XueChengError.cast("收费规则不能为空");
        }
        if(courseMarket.getCharge().equals("201001")){
            if(courseMarket.getPrice()==null||courseMarket.getPrice()<0){
                XueChengError.cast("课程收费必须大于0且不为空");}
        }
        CourseMarket courseMarketNew = courseMarketMapper.selectById(courseMarket.getId());
        if(courseMarketNew==null){
            return courseMarketMapper.insert(courseMarket);
        }
        else{
            System.out.println(courseMarket.getPrice());
            System.out.println(courseMarketNew.getPrice());
            BeanUtils.copyProperties(courseMarket, courseMarketNew);
            courseMarketNew.setId(courseMarket.getId());
            System.out.println(courseMarketNew.getPrice());
            return courseMarketMapper.updateById(courseMarketNew);
        }
    }
    private QueryCourseMarketDto getCourseMarket(long courseId) {
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if(courseBase == null){
            return null;
        }
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        System.out.println(courseMarket.getPrice());
        QueryCourseMarketDto queryCourseMarketDto = new QueryCourseMarketDto();
        BeanUtils.copyProperties(courseBase, queryCourseMarketDto);
        if(courseMarket!=null){
            BeanUtils.copyProperties(courseMarket, queryCourseMarketDto);
        }
        System.out.println(queryCourseMarketDto.getPrice());
        CourseCategory courseCategoryBySt = courseCategoryMapper.selectById(courseBase.getSt());
        queryCourseMarketDto.setStName(courseCategoryBySt.getName());
        CourseCategory courseCategoryByMt = courseCategoryMapper.selectById(courseBase.getMt());
        queryCourseMarketDto.setMtName(courseCategoryByMt.getName());

        return queryCourseMarketDto;

    }

    @Override
    @Transactional
    public void deletecourse(long courseId) {
        try {
            courseBaseMapper.deleteById(courseId);
            courseMarketMapper.deleteById(courseId);
            LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper();
            queryWrapper.eq(Teachplan::getCourseId, courseId);
            teachplanMapper.delete(queryWrapper);
            LambdaQueryWrapper<TeachplanMedia> queryWrapperm = new LambdaQueryWrapper();
            queryWrapperm.eq(TeachplanMedia::getCourseId, courseId);
            teachplanMediaMapper.delete(queryWrapperm);
            LambdaQueryWrapper<CourseTeacher> queryWrappert = new LambdaQueryWrapper();
            queryWrappert.eq(CourseTeacher::getCourseId, courseId);
            courseTeacherMapper.delete(queryWrappert);
        }catch(Exception e){
            XueChengError.cast("数据库中不存在课程数据");
        }
    }
}
