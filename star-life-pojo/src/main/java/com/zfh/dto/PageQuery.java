package com.zfh.dto;

import lombok.Data;

/**
 * 分页查询参数
 */
@Data
public abstract class PageQuery {
    /**
     * 当前页码
     */
    private Integer currentPage;
    /**
     * 每页显示数量
     */
    private Integer pageSize;
}
