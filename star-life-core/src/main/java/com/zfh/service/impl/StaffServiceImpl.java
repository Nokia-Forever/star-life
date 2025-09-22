package com.zfh.service.impl;

import com.zfh.entity.Staff;
import com.zfh.mapper.StaffMapper;
import com.zfh.service.IStaffService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 店员关联表 服务实现类
 * </p>
 *
 * @author author
 * @since 2025-09-22
 */
@Service
public class StaffServiceImpl extends ServiceImpl<StaffMapper, Staff> implements IStaffService {

}
