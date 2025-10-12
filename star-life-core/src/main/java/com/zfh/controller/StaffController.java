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
     * 添加店员
     * @param staffDto
     * @return
     */
    @PostMapping("/shop/role")
    public R addStaff(@RequestBody @Validated StaffDto staffDto){
        return R.OK(staffService.addStaff(staffDto));
    }

    /**
     * 修改职员角色
     * @param staffDto
     * @return
     */
    @PutMapping("/shop/role")
    public R updateStaff(@RequestBody @Validated StaffDto staffDto){
        return R.OK(staffService.updateStaff(staffDto));
    }

    /**
     * 获取店员列表
     * @param shopId
     * @return
     */
    @GetMapping("/white/shop/role/list")
    public R getStaffList(@RequestParam Long shopId){
        return R.OK(staffService.getStaffList(shopId));
    }

    /**
     * 获取店员信息
     * @param shopId
     * @param id
     * @return
     */
    @GetMapping("/white/shop/role/{shopId}/{id}")
    public R getStaffInfo(@PathVariable Long shopId,@PathVariable Long id){
        return R.OK(staffService.getStaffInfo(shopId,id));
    }

    //TODO 删除员工
    //TODO 分页查询
}
