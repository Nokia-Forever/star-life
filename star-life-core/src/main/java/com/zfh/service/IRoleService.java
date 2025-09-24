package com.zfh.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zfh.entity.Role;

import java.util.Map;

/**
 * <p>
 * 角色定义表 服务类
 * </p>
 *
 * @author author
 * @since 2025-09-22
 */
public interface IRoleService extends IService<Role> {

    /**
     * 获取角色列表
     *
     * @return
     */
    Map<Long, Map> listMap();
}
