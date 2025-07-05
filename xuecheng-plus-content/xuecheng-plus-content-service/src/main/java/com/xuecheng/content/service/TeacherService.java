package com.xuecheng.content.service;

import com.xuecheng.content.model.po.CourseTeacher;

import java.util.List;

public interface TeacherService {
    public List<CourseTeacher> getCourseTeacher(long courseId);
    public CourseTeacher addCourseTeacher(CourseTeacher courseTeacher);
    public CourseTeacher updateCourseTeacher(CourseTeacher courseTeacher);
    public void deleteTeacherById(long courseId,long id);
}
