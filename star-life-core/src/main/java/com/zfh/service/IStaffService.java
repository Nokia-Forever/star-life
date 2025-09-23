package com.zfh.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zfh.dto.StaffDto;
import com.zfh.entity.Role;
import com.zfh.entity.Staff;
import com.zfh.vo.ShopRoleVo;

import java.util.List;

/**
 * <p>
 * 店员关联表 服务类
 * </p>
 *
 * @author author
 * @since 2025-09-22
 */
public interface IStaffService extends IService<Staff> {

    /**
     * 获取当前用户角色下权重比自己小
     * @param shopId
     * @param roleId
     * @return
     */
    List<Role> getRoleLessPowerList(Long shopId, Long roleId);

    /**
     * 获取店员关联的店铺列表
     * @return
     */
    List<ShopRoleVo> getShopList();

    /**
     * 添加店员
     * @param staffDto
     * @return
     */
    int addStaff(StaffDto staffDto);
}
