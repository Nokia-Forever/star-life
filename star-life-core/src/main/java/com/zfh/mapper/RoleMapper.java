package com.zfh.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zfh.entity.Role;
import org.apache.ibatis.annotations.MapKey;

import java.util.Map;

/**
 * <p>
 * 角色定义表 Mapper 接口
 * </p>
 *
 * @author author
 * @since 2025-09-22
 */
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 获取角色列表
     * @return
     */
    @MapKey("id")
    Map<Long, Map> listMap();
}
