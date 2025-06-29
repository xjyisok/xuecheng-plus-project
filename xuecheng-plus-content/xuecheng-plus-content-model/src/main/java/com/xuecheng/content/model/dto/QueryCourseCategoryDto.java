package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;

@Data
@Slf4j
@Schema(name = "QueryCourseCategoryDto", description = "课程类别请求对象")
public class QueryCourseCategoryDto extends CourseCategory implements Serializable {
    List<QueryCourseCategoryDto> childrenTreeNodes;
}
