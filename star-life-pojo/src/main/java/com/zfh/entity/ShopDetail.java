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
 * 商铺详情表
 * </p>
 *
 * @author author
 * @since 2025-09-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("shop_detail")
public class ShopDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联商铺ID
     */
    @TableId(value = "shop_id", type = IdType.AUTO)
    private Long shopId;

    /**
     * 详细描述
     */
    private String description;

    /**
     * 营业时间（JSON格式，如{"周一":"9:00-22:00"}）
     */
    private String businessHours;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


}
