package com.xuecheng.content;

import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.QueryCourseCategoryDto;
import com.xuecheng.content.model.po.CourseCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class CourseCategoryMapperTest {
    @Autowired
    CourseCategoryMapper courseCategoryMapper;
    @Test
    public void testcategory() {
        List<QueryCourseCategoryDto>result=courseCategoryMapper.QueryCourseCategory("1");
        System.out.println(result);
    }
}
