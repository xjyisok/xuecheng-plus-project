package com.xuecheng.content.service.impl;

import com.xuecheng.content.mapper.CoursePublishMapper;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.dto.QueryCourseMarketDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.service.AddCourseService;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.content.service.CoursebaseInfoService;
import com.xuecheng.content.service.TeachPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoursePublishServiceImpl implements CoursePublishService {
    @Autowired
    AddCourseService addCourseService;

    @Autowired
    TeachPlanService teachplanService;

    @Override
    public CoursePreviewDto getCoursePreviewInfo(Long courseId) {
        QueryCourseMarketDto courseBaseInfo = addCourseService.getCourseMarket(courseId);

        //课程计划信息
        List<TeachplanDto> teachplanTree = teachplanService.getTeachplan(courseId);

        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        coursePreviewDto.setCourseBase(courseBaseInfo);
        coursePreviewDto.setTeachplans(teachplanTree);
        return coursePreviewDto;
    }
        }
