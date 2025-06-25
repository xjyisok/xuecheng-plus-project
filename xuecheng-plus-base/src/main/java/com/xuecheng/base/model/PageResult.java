package com.xuecheng.base.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import java.util.List;
@Data
@ToString
@Schema(name="PageResult",description = "返回对象")
public class PageResult<T> {
    public List<T> items;
    private long counts;
    private long page;
    private long pageSize;
    public PageResult() {}
    public PageResult(List<T> items, Long counts, Long page, Long pageSize) {
        this.items = items;
        this.counts = counts;
        this.page = page;
        this.pageSize = pageSize;
    }
}
