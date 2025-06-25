package com.xuecheng.content.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Schema(name = "QueryCourseParamsDto", description = "课程信息请求对象")
public class QueryCourseParamsDto {
    String auditStatus;
    String coursename;
    String publishStatus;
    public QueryCourseParamsDto() {}
    public QueryCourseParamsDto(String auditStatus, String coursename, String publishStatus) {
        this.auditStatus = auditStatus;
        this.coursename = coursename;
        this.publishStatus = publishStatus;
    }
}
