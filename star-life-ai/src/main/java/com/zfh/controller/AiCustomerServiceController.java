package com.zfh.controller;


import com.zfh.dto.AiCustomerServiceDto;
import com.zfh.result.R;
import com.zfh.service.IAiCustomerServiceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *AI客服controller
 * @author author
 * @since 2025-10-12
 */
@RestController
@RequestMapping("/client/ai-customerService")
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
}
