package com.zfh.mapper;

import com.zfh.entity.Staff;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 店员关联表 Mapper 接口
 * </p>
 *
 * @author author
 * @since 2025-09-22
 */
public interface StaffMapper extends BaseMapper<Staff> {

    /**
     * 根据店铺id查询店员ids
     * @param shopId
     * @return
     */
    List<Long> getIdsByShopId(Long shopId);
}
