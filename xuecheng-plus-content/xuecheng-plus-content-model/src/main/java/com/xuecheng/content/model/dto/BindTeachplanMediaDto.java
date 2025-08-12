package com.xuecheng.content.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "BindTeachplanMediaDto", description = "教学计划媒资绑定提交数据")
public class BindTeachplanMediaDto {

    @Schema(description = "媒资文件id", required = true)
    private String mediaId;

    @Schema(description = "媒资文件名称", required = true)
    private String fileName;

    @Schema(description = "课程计划标识", required = true)
    private Long teachplanId;
}
