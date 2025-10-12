package com.zfh.vo;

import com.zfh.entity.SeckillVoucher;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 优惠券vo
 */
@Data
public class VoucherVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 商铺id
     */
    private Long shopId;

    /**
     * 代金券标题
     */
    private String title;

    /**
     * 副标题
     */
    private String subTitle;

    /**
     * 使用规则
     */
    private String rules;

    /**
     * 支付金额，单位是分。例如200代表2元
     */
    private Long payValue;

    /**
     * 抵扣金额，单位是分。例如200代表2元
     */
    private Long actualValue;

    /**
     * 0,普通券；1,秒杀券
     */
    private Integer type;

    /**
     * 1,上架; 2,下架; 3,过期
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 秒杀优惠券信息
     */
    private SeckillVoucher seckillVoucher;

}
