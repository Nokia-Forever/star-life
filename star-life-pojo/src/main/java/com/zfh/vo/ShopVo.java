package com.zfh.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.zfh.entity.ShopDetail;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 店铺详情信息
 */
@Data
public class ShopVo implements Serializable {
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
     * 状态 0-关闭，1-上线
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

    /**
     * 店铺详情
     */
    private ShopDetail shopDetail;

    /**
     * 营业状态
     */
    private Boolean businessStatus;
}
