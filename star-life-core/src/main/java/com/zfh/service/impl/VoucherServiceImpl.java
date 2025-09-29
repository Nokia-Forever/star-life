package com.zfh.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zfh.constant.VoucherConstant;
import com.zfh.dto.SeckillVoucherDto;
import com.zfh.dto.VoucherDto;
import com.zfh.dto.VoucherStatusDto;
import com.zfh.entity.SeckillVoucher;
import com.zfh.entity.Voucher;
import com.zfh.mapper.VoucherMapper;
import com.zfh.service.ISeckillVoucherService;
import com.zfh.service.IVoucherService;
import com.zfh.vo.VoucherVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author author
 * @since 2025-09-29
 */
@Service
public class VoucherServiceImpl extends ServiceImpl<VoucherMapper, Voucher> implements IVoucherService {
    @Autowired
    private ISeckillVoucherService seckillVoucherService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private VoucherMapper voucherMapper;

    /**
     * 新增普通优惠券
     *
     * @param voucherDto
     * @return
     */
    @Override
    public Boolean add(VoucherDto voucherDto) {
        Date now = new Date();
        Voucher voucher = new Voucher();
        BeanUtils.copyProperties(voucherDto, voucher);
        voucher.setType(VoucherConstant.COMMON_VOUCHER);
        voucher.setStatus(VoucherConstant.VOUCHER_UP);
        voucher.setCreateTime(now);
        voucher.setUpdateTime(now);
        return save(voucher);
    }

    /**
     * 新增秒杀优惠券
     *
     * @param seckillVoucherDto
     * @return
     */
    @Transactional
    @Override
    public Boolean addSeckill(SeckillVoucherDto seckillVoucherDto) {
        Date now = new Date();
        Voucher voucher = new Voucher();
        BeanUtils.copyProperties(seckillVoucherDto, voucher);
        voucher.setType(VoucherConstant.SECKILL_VOUCHER);
        voucher.setStatus(VoucherConstant.VOUCHER_UP);
        voucher.setCreateTime(now);
        voucher.setUpdateTime(now);
        save(voucher);

        SeckillVoucher seckillVoucher = new SeckillVoucher();
        seckillVoucher.setVoucherId(voucher.getId());
        seckillVoucher.setStock(seckillVoucherDto.getStock());
        seckillVoucher.setBeginTime(seckillVoucherDto.getBeginTime());
        seckillVoucher.setEndTime(seckillVoucherDto.getEndTime());
        seckillVoucher.setUpdateTime(now);
        seckillVoucher.setCreateTime(now);
        return seckillVoucherService.save(seckillVoucher);
    }

    /**
     * 员工查询店铺优惠卷信息
     *
     * @param shopId
     * @return
     */
    @Override
    public List<VoucherVo> listByShopId(Long shopId) {
        //查询所有优惠券
        return voucherMapper.listAllByShopId(shopId);
    }

    /**
     * 修改优惠券状态
     *
     * @param seckillVoucherDto
     * @return
     */
    @Override
    public Boolean changeStatus(VoucherStatusDto seckillVoucherDto) {
        Integer statusInt = seckillVoucherDto.getStatus() ? VoucherConstant.VOUCHER_UP : VoucherConstant.VOUCHER_DOWN;
        return update(new LambdaUpdateWrapper<Voucher>().set(Voucher::getStatus, statusInt)
                .eq(Voucher::getId, seckillVoucherDto.getId()));
    }

    /**
     * 获取店铺的优惠券列表
     * @param shopId
     * @return
     */
    @Override
    public List<VoucherVo> listOnlineByShopId(Long shopId) {
        return voucherMapper.listOnlineByShopId(shopId);
    }

    /**
     * 获取优惠券信息
     * @param id
     * @return
     */
    @Override
    public VoucherVo getInfoById(Long id) {
        return voucherMapper.getInfoById(id);
    }
}
