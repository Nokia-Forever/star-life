package com.zfh.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zfh.dto.StaffDto;
import com.zfh.entity.Role;
import com.zfh.entity.Staff;
import com.zfh.vo.UserRoleVo;

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
     * 添加店员
     * @param staffDto
     * @return
     */
    int addStaff(StaffDto staffDto);

    /**
     * 修改职员角色
     * @param staffDto
     * @return
     */
    int updateStaff(StaffDto staffDto);

    /**
     * 获取店员列表
     * @param shopId
     * @return
     */
    List<UserRoleVo> getStaffList(Long shopId);

    /**
     * 获取店员信息
     * @param shopId
     * @param id
     * @return
     */
    UserRoleVo getStaffInfo(Long shopId, Long id);
}
