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
 * 客服对话记录表
 * </p>
 *
 * @author author
 * @since 2025-10-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("customer_service_chat")
public class CustomerServiceChat implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键（唯一标识）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 店铺ID（关联shop.id）
     */
    private Long shopId;

    /**
     * 发送人ID（关联user.id）
     */
    private Long senderId;

    /**
     * 接收人ID（关联user.id）
     */
    private Long receiverId;

    /**
     * 信息内容
     */
    private String content;

    /**
     * 信息类型：0-用户给商家，1-商家给用户
     */
    private Integer messageType;

    /**
     * 创建时间
     */
    private Date createTime;


}
