package com.zfh.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 店员关联表
 * </p>
 *
 * @author author
 * @since 2025-09-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("staff")
public class Staff implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID（关联user.id）
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    /**
     * 店铺ID（关联shop.id）
     */
    private Long shopId;

    /**
     * 角色ID（关联role.id）
     */
    private Long roleId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


}
