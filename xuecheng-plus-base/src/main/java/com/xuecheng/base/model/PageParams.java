package com.xuecheng.base.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Schema(name = "PageParams", description = "分页请求对象")
public class PageParams {
    private long pageNo;
    private long pageSize;
    public PageParams() {
    }
    public PageParams(Long pageNo, Long pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }
}
