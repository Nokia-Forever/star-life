package com.zfh.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 粉丝列表查询参数
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class IdPageDto extends PageQuery{
    /**
     * 用户id
     */
    private Long id;
}
