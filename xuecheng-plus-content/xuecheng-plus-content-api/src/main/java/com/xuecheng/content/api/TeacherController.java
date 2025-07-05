package com.xuecheng.content.api;

import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.TeacherService;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TeacherController {
    @Autowired
    private TeacherService teacherService;
    @GetMapping("/courseTeacher/list/{courseId}")
    public List<CourseTeacher> courseTeacher(@PathVariable long courseId) {
        return teacherService.getCourseTeacher(courseId);
    }
    @PostMapping("/courseTeacher")
    public CourseTeacher addCourseTeacher(@RequestBody CourseTeacher courseTeacher) {
        return teacherService.addCourseTeacher(courseTeacher);
    }
    @PutMapping("/courseTeacher")
    public CourseTeacher updateCourseTeacher(@RequestBody CourseTeacher courseTeacher) {
        return teacherService.updateCourseTeacher(courseTeacher);
    }
    @DeleteMapping("/courseTeacher/course/{courseId}/{id}")
    public void deleteCourseTeacher(@PathVariable long courseId, @PathVariable long id) {
        teacherService.deleteTeacherById(courseId, id);
    }
}
