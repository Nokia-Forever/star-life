package com.zfh.controller;


import com.zfh.dto.IdPageDto;
import com.zfh.result.R;
import com.zfh.service.ICustomerServiceChatService;
import com.zfh.utils.CurrentHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**

 * 客服对话记录 controller
 *
 * @author author
 * @since 2025-10-14
 */
@RestController
@RequestMapping("/client/customer-service-chat")
public class CustomerServiceChatController {
    @Autowired
    private ICustomerServiceChatService customerServiceChatService;

    /**
     * 根据店铺id获取当前用户聊天记录列表
     * @param idPageDto
     * @return
     */
    @RequestMapping("/list")
    public R list(@RequestBody IdPageDto idPageDto) {
        //获取当前登录用户
        Long id = CurrentHolder.getCurrentUser().getId();
        return R.OK(customerServiceChatService.listByShopIdAndUserId(idPageDto.getId(),id,idPageDto.getCurrentPage(),idPageDto.getPageSize()));
    }

}
