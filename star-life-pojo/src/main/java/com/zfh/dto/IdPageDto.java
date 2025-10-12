package com.zfh.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 粉丝列表查询参数
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class IdPageDto extends PageQuery implements Serializable {
    /**
     * 用户id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}
