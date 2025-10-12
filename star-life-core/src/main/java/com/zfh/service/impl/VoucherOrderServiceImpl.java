package com.zfh.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zfh.constant.ExceptionConstant;
import com.zfh.constant.RedisKeyConstant;
import com.zfh.entity.SeckillVoucher;
import com.zfh.entity.User;
import com.zfh.entity.VoucherOrder;
import com.zfh.exception.VoucherOrderException;
import com.zfh.mapper.VoucherOrderMapper;
import com.zfh.service.ISeckillVoucherService;
import com.zfh.service.IVoucherOrderService;
import com.zfh.utils.CurrentHolder;
import com.zfh.utils.RedisWorker;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author author
 * @since 2025-09-29
 */
@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {

    @Autowired
    private ISeckillVoucherService seckillVoucherService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisWorker redisWorker;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private RedissonClient redissonClient;

    private  VoucherOrderServiceImpl proxy ;

    //lua脚本
    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;

    static {
        SECKILL_SCRIPT = new DefaultRedisScript<Long>();
        SECKILL_SCRIPT.setLocation(new ClassPathResource("lua/Seckill.lua"));
        SECKILL_SCRIPT.setResultType(Long.class);
    }
    /**
     * 秒杀优惠券
     * @param voucherId
     * @return
     */
    @Transactional
    @Override
    public Long seckillVoucher(Long voucherId) {
        //获取优惠卷
        SeckillVoucher seckillVoucher = seckillVoucherService.getById(voucherId);
        if (seckillVoucher == null) {
            throw new VoucherOrderException(ExceptionConstant.VOUCHER_NOT_EXIST);
        }
        Date now = new Date();
        //判断时间
        if (seckillVoucher.getBeginTime().after(now)) {
            throw new VoucherOrderException(ExceptionConstant.VOUCHER_NOT_START);
        }
        if (seckillVoucher.getEndTime().before(now)) {
            throw new VoucherOrderException(ExceptionConstant.VOUCHER_HAD_END);
        }
        //获取当前用户
        User user = CurrentHolder.getCurrentUser();

        //判断库存
        Long res = stringRedisTemplate.execute(SECKILL_SCRIPT,
                List.of(RedisKeyConstant.SECKILL_STOCK_KEY + voucherId, RedisKeyConstant.SECKILL_ORDER_KEY + voucherId),
                user.getId().toString());
        if (res == null || res == 1|| res == 3 ) {
            throw new VoucherOrderException(ExceptionConstant.VOUCHER_NOT_ENOUGH);
        }
        if (res != 0) {
            throw new VoucherOrderException(ExceptionConstant.USER_HAD_ORDER);
        }

        //生成唯一ID
        Long orderId = redisWorker.getNextId(RedisKeyConstant.SECKILL_ID_KEY);
        //异步创建订单
        createOrderAsync(voucherId, orderId, user);
        proxy = (VoucherOrderServiceImpl) AopContext.currentProxy();
        return orderId;
        //TODO 未完善,支付功能
    }

    @Async("threadPoolExecutor")
    public void createOrderAsync(Long voucherId, Long orderId, User user) {
        threadPoolExecutor.execute(() -> {
            RLock lock = redissonClient.getLock(RedisKeyConstant.SECKILL_LOCK_KEY + user.getId());
            try {
                if(lock.tryLock()){
                    proxy.createOrder(voucherId, orderId, user);
                }else {
                    throw new VoucherOrderException(ExceptionConstant.USER_HAD_ORDER);
                }
            }finally {
                lock.unlock();
            }
        });
    }

    @Transactional
    public void createOrder(Long voucherId, Long orderId, User user) {
        VoucherOrder voucherOrder = new VoucherOrder();
        voucherOrder.setId(orderId);
        voucherOrder.setUserId(user.getId());
        voucherOrder.setVoucherId(voucherId);
        voucherOrder.setCreateTime(new Date());
        voucherOrder.setUpdateTime(new Date());
        //查询是否存在该用户的订单
        Long count = query()
                .eq("user_id", voucherOrder.getUserId())
                .eq("voucher_id", voucherOrder.getVoucherId()).count();
        if (count > 0) {
            throw new VoucherOrderException(ExceptionConstant.USER_HAD_ORDER);
        }
        //扣减库存
        boolean success = seckillVoucherService.update()
                .setSql("stock = stock - 1")
                .eq("voucher_id", voucherOrder.getVoucherId())
                //乐观锁
                .gt("stock", 0)
                .update();
        if (!success) {
            throw new VoucherOrderException(ExceptionConstant.VOUCHER_NOT_ENOUGH);
        }
        save(voucherOrder);
    }
}
