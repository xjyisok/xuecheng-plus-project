package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.QueryCourseCategoryDto;
import org.springframework.stereotype.Service;

import java.util.List;
public interface CoursecategoryqueryService {
    public List<QueryCourseCategoryDto> queryCourseCategory(String id);
}
