package com.zfh.controller;


import com.zfh.dto.StaffDto;
import com.zfh.result.R;
import com.zfh.service.IStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**

 * 店员controller
 *
 * @author author
 * @since 2025-09-22
 */
@RestController
@RequestMapping("/client/staff")
public class StaffController {
    @Autowired
    private IStaffService staffService;

    /**
     * 获取当前用户角色下权重比自己小
     * @param shopId
     * @param roleId
     * @return
     */
    @GetMapping("/role/lesspower/{shopId}/{roleId}")
    public R getRoleLessPowerList(@PathVariable Long shopId , @PathVariable Long roleId){
        return R.OK(staffService.getRoleLessPowerList(shopId, roleId));
    }


    /**
     * 当前用户获取管理的店铺的信息
     * @return
     */
    @GetMapping("/shop/list")
    public R getShopList(){
        return R.OK(staffService.getShopList());
    }


    /**
     * 添加店员
     * @param staffDto
     * @return
     */
    @PostMapping("/shop/role")
    public R addStaff(@RequestBody @Validated StaffDto staffDto){
        return R.OK(staffService.addStaff(staffDto));
    }

}
