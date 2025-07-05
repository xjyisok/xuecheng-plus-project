package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.TeacherService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TeacherserviceImpl implements TeacherService {
    @Autowired
    CourseTeacherMapper courseTeacherMapper;
    @Override
    public List<CourseTeacher> getCourseTeacher(long courseId) {
        LambdaQueryWrapper<CourseTeacher>queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId, courseId);
        List<CourseTeacher>courserteacherlist=courseTeacherMapper.selectList(queryWrapper);
        return courserteacherlist;
    }

    @Override
    public CourseTeacher addCourseTeacher(CourseTeacher courseTeacher) {
        CourseTeacher courseTeachernew = new CourseTeacher();
        BeanUtils.copyProperties(courseTeacher, courseTeachernew);
        courseTeachernew.setCreateDate(LocalDateTime.now());
        courseTeacherMapper.insert(courseTeachernew);
        return courseTeachernew;
    }
    @Override
    public CourseTeacher updateCourseTeacher(CourseTeacher courseTeacher) {
        CourseTeacher courseTeachernew = new CourseTeacher();
        BeanUtils.copyProperties(courseTeacher, courseTeachernew);
        courseTeacherMapper.updateById(courseTeachernew);
        return courseTeachernew;
    }

    @Override
    public void deleteTeacherById(long courseId, long id) {
        LambdaQueryWrapper<CourseTeacher>queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId, courseId);
        queryWrapper.eq(CourseTeacher::getId, id);
        courseTeacherMapper.delete(queryWrapper);
    }
}
