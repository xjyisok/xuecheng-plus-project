package com.xuecheng.content.model.dto;

import com.xuecheng.base.exception.ValidationGroups;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ModifyCourseDto extends AddCourseParamDto{
    @NotNull(message = "课程id不能为空")
    private Long id;
}
