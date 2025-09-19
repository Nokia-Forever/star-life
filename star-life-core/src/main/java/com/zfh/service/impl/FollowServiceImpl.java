package com.zfh.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zfh.constant.ExceptionConstant;
import com.zfh.constant.RedisKeyConstant;
import com.zfh.dto.IdPageDto;
import com.zfh.entity.Follow;
import com.zfh.exception.BaseException;
import com.zfh.mapper.FollowMapper;
import com.zfh.result.RPage;
import com.zfh.service.IFollowService;
import com.zfh.service.IUserInfoService;
import com.zfh.service.IUserService;
import com.zfh.utils.CurrentHolder;
import com.zfh.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 用户关注表 服务实现类
 * </p>
 *
 * @author author
 * @since 2025-09-19
 */
@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements IFollowService {
    @Autowired
    private IUserInfoService userInfoService;
    @Autowired
    private FollowMapper followMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private IUserService userService;

    //lua脚本
    private static final DefaultRedisScript<Long> FOLLOW_SCRIPT;
    static {
        FOLLOW_SCRIPT = new DefaultRedisScript<Long>();
        FOLLOW_SCRIPT.setLocation(new ClassPathResource("follow.lua"));
        FOLLOW_SCRIPT.setResultType(Long.class);
    }


    /**
     * 关注或取消关注
     * @param id
     * @param isFollow
     * @return
     */
    @Override
    @Transactional
    public int follow(Long id, Boolean isFollow) {
        //获取当前 用户
        Long currentUserId = CurrentHolder.getCurrentUser().getId();
        if(currentUserId.equals(id)){
            throw new BaseException(ExceptionConstant.CANNOT_FOLLOW_YOURSELF);
        }
        Follow follow = new Follow();
        follow.setUserId(id);
        follow.setFanId(currentUserId);
        follow.setCreateTime(new Date());

        // redis操作(lua脚本)
        boolean res = Objects.requireNonNull(stringRedisTemplate.execute(FOLLOW_SCRIPT,
                List.of(RedisKeyConstant.USER_FOLLOW_KEY + currentUserId.toString()),
                id.toString(), RedisKeyConstant.USER_FOLLOW_EXPIRE_TIME.toString(), isFollow.toString())).intValue() == 1L;

        if(!res){
            throw  new BaseException(ExceptionConstant.OPERATION_FAILED);
        }
        //操作成功
        if(isFollow ){
            //数据库更新
            save( follow);
            //粉丝数量+1
            userInfoService.changeFansCount(id, 1);
            //关注数量+1
            userInfoService.changeFollowingCount(currentUserId, 1);
        }
        else{
            LambdaQueryWrapper<Follow> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Follow::getFanId, currentUserId)
                    .eq(Follow::getUserId,id);
            followMapper.delete( wrapper);
            //粉丝数量-1
            userInfoService.changeFansCount(id, -1);
            //关注数量-1
            userInfoService.changeFollowingCount(currentUserId, -1);
        }
        return 1;
    }

    /**
     * 判断当前用户是否关注了该用户
     * @param id
     * @return
     */
    @Override
    public Boolean isFollow(Long id) {
        Long currentUserId = CurrentHolder.getCurrentUser().getId();
        //直接在redis判断
        return stringRedisTemplate.opsForSet().isMember(RedisKeyConstant.USER_FOLLOW_KEY +currentUserId.toString(), id.toString());
    }

    /**
     * 获取粉丝列表
     * @param idPageDto
     * @return
     */
    @Override
    public RPage<UserVo> listFans(IdPageDto idPageDto) {
        IPage<Long> ids = followMapper.selectFanIdsByUserId(new Page<>(idPageDto.getCurrentPage(), idPageDto.getPageSize()), idPageDto.getId());
        List<UserVo> userVoList = userService.selectUserVoListByIds(ids.getRecords());
        return new RPage<>(ids.getTotal(), userVoList);
    }
}
