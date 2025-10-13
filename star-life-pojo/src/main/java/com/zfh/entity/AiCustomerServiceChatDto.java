package com.zfh.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class AiCustomerServiceChatDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 会话ID
     */
    private String sessionId ;

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
}
