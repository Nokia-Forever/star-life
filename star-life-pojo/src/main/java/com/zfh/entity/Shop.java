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
 * 商铺基础信息表
 * </p>
 *
 * @author author
 * @since 2025-09-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("shop")
public class Shop implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商铺ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联商家用户ID（对应user.id）
     */
    private Long userId;

    /**
     * 商铺名称
     */
    private String name;

    /**
     * 商铺类型ID（关联shop_type.id）
     */
    private Long typeId;

    /**
     * 地址
     */
    private String address;

    /**
     * 状态：0-关闭，1-营业
     */
    private Integer status;

    /**
     * 封面图URL
     */
    private String coverImage;

    /**
     * 均价，取整数
     */
    private Long avgPrice;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 销量
     */
    private Integer sold;

    /**
     * 评分（评分*10,方便处理,0-5分）
     */
    private Integer rating;

    /**
     * 评价数量
     */
    private Integer reviewCount;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
