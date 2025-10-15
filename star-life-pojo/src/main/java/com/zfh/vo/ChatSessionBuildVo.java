package com.zfh.vo;

import com.zfh.entity.CustomerServiceChat;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/*
会话创建返回Vo
 */
@Data
public class ChatSessionBuildVo implements Serializable {
    private static final long serialVersionUID = 1L;
    /*
    会话id
     */
    private String sessionId;
    /*
    历史聊天记录
     */
    private List<CustomerServiceChat> customerServiceChats;
}
