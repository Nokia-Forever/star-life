package com.zfh.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页查询参数
 */
@Data
public abstract class PageQuery  implements Serializable {
    /**
     * 当前页码
     */
    private Integer currentPage;
    /**
     * 每页显示数量
     */
    private Integer pageSize;

    private static final long serialVersionUID = 1L;
}
