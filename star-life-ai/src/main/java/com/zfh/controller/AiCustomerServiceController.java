package com.zfh.controller;


import com.zfh.dto.AiCustomerServiceDto;
import com.zfh.result.R;
import com.zfh.service.IAiCustomerServiceService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 *AI客服controller
 * @author author
 * @since 2025-10-12
 */
@RestController
@RequestMapping("/client/ai-customerService")
@Slf4j
public class AiCustomerServiceController {
    @Autowired
    private IAiCustomerServiceService aiCustomerServiceService;


    /**
     * 添加AI客服
     * @return
     * @param aiCustomerServiceDto
     */
    @PreAuthorize("hasAnyRole(#aiCustomerServiceDto.shopId + '_' + T(com.zfh.constant.StaffConstant).CEO," +
            "#aiCustomerServiceDto.shopId + '_' + T(com.zfh.constant.StaffConstant).Manger)")
    @PostMapping
    public R addAiCustomerService(@RequestBody @Valid AiCustomerServiceDto aiCustomerServiceDto){
        return R.OK(aiCustomerServiceService.addAiCustomerService(aiCustomerServiceDto));
    }


    /**
     *人工客服上线
     * @param shopId
     * @return
     */
    @PreAuthorize("hasAnyRole(#shopId + '_' + T(com.zfh.constant.StaffConstant).CEO," +
            "#shopId + '_' + T(com.zfh.constant.StaffConstant).Manger," +
            "#shopId + '_' + T(com.zfh.constant.StaffConstant).Salesclerk," +
            "#shopId+ '_' + T(com.zfh.constant.StaffConstant).CustomerService) ")
    @GetMapping("/online/{shopId}")
    public R huManOnline(@PathVariable Long shopId){
        return R.OK(aiCustomerServiceService.huManOnline(shopId));
    }

    /**
     * 建立客服会话
     * @param
     * @return
     */
    @PostMapping("/chat/{shopId}")
    public R buildChatSession(@PathVariable Long shopId){
        return R.OK(aiCustomerServiceService.buildChatSession(shopId));
    }


    /**
     * 关闭会话
     * @param sessionId
     * @return
     */
    @DeleteMapping("/closeChatSession/{sessionId}")
    public R closeChatSession(@PathVariable String sessionId){
        return R.OK(aiCustomerServiceService.closeChatSession(sessionId));
    }
}
