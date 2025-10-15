package com.zfh.tools;


import com.zfh.constant.RedisKeyConstant;
import com.zfh.websocket.CustomerServiceSessionManager;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 客服工具类
 */
@Component
public class CustomerServiceTools {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    //TODO 硬编码
    @Tool(description = "当用户提出要人工服务或者人工客服或者需要人为接入等类似问题时你就需要这个方法,这是一个接入人工客服的工具," +
            "他会告诉你接入的客服的id,如果是-1,那么说明客服在繁忙,你需要给用户温馨提醒,让他耐心等待;" +
            "如果是其他,就说明接入人工客服成功,你需要在回答中开始写入固定格式的语句:'客服id:{}',{}代替为所获取的customerServiceId")
    public Long accessCustomerService(@ToolParam(description = "会话Id,对应的是客户发送的信息里面的sessionId,是字符串格式的") String sessionId,
                                      @ToolParam(description = "用户Id") Long userId,@ToolParam(description = "店铺id") Long shopId) throws IOException {
        //现根据shopId查询出ai客服信息
        //获取一个客服
        Long customerServiceId = CustomerServiceSessionManager.getOneCustomerService(shopId);
        if(customerServiceId == -1L){
            return -1L;
        }
        //redis改变会话对象
        stringRedisTemplate.opsForValue().set(RedisKeyConstant.USER_SESSION_KEY+sessionId,
                shopId+":"+customerServiceId+":"+userId,RedisKeyConstant.USER_SESSION_EXPIRE_TIME, TimeUnit.MILLISECONDS);

        //并提醒客服
        CustomerServiceSessionManager.sendToCustomerService(customerServiceId,
                "用户Id:"+userId+"接入人工客服");
        //并获取用户之前的会话记录
        List<String> list = stringRedisTemplate.opsForList().range(RedisKeyConstant.USER_SESSION_LOG_KEY + sessionId, 0, -1);
        //并删除旧会话记录
        stringRedisTemplate.delete(RedisKeyConstant.USER_SESSION_LOG_KEY + sessionId);
        //用户存放会话id
        stringRedisTemplate.opsForSet().add(RedisKeyConstant.USER_SESSION_ID_LIST_KEY+customerServiceId,sessionId);
        if(list != null){
            //发送给客服
            CustomerServiceSessionManager.sendToCustomerService(customerServiceId,list.toArray(String[]::new));
        }
        return customerServiceId;
    }

}