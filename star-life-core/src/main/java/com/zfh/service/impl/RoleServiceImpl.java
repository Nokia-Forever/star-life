package com.zfh.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zfh.entity.Role;
import com.zfh.mapper.RoleMapper;
import com.zfh.service.IRoleService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * <p>
 * 角色定义表 服务实现类
 * </p>
 *
 * @author author
 * @since 2025-09-22
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    /**
     * 获取角色列表
     *
     * @return
     */
    @Override
    public Map<Long, Map> listMap() {
        return baseMapper.listMap();
    }
}
