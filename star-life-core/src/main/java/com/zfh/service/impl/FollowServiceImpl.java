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
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
                id.toString(), RedisKeyConstant.USER_FOLLOW_EXPIRE_TIME.toString(), isFollow.toString()))
                .intValue() == 1L;

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
            followMapper.delete( new LambdaQueryWrapper<Follow>().eq(Follow::getFanId, currentUserId)
                    .eq(Follow::getUserId,id));
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
        List<Long> ids = getFollowList(currentUserId);
        return ids.contains(id);
    }

    //获取关注列表(Redis)
    private List<Long> getFollowList(Long id) {
        //判断有没有这个key
        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(RedisKeyConstant.USER_FOLLOW_KEY + id))){
            Set<String> members = stringRedisTemplate.opsForSet().members(RedisKeyConstant.USER_FOLLOW_KEY + id);
            stringRedisTemplate.expire(RedisKeyConstant.USER_FOLLOW_KEY + id,
                    RedisKeyConstant.USER_FOLLOW_EXPIRE_TIME, TimeUnit.MILLISECONDS);
            return members.stream().filter(pid-> !pid.equals("-1")).map(Long::valueOf).toList();
        }
        //查询数据库,并缓存到redis
        List<Long> ids = followMapper.selectAllFollowIdById(id);
        List<String> stringIds = ids.stream().map(String::valueOf).toList();
        //如果没有关注
        if(ids.isEmpty()){
            stringRedisTemplate.opsForSet().add(RedisKeyConstant.USER_FOLLOW_KEY + id, "-1");
            stringRedisTemplate.expire(RedisKeyConstant.USER_FOLLOW_KEY + id,
                    RedisKeyConstant.USER_FOLLOW_EXPIRE_TIME, TimeUnit.MILLISECONDS);
            return List.of();
        }
        //一次性添加到redis
        stringRedisTemplate.opsForSet().add(RedisKeyConstant.USER_FOLLOW_KEY + id,stringIds.toArray(new String[0]));
        stringRedisTemplate.expire(RedisKeyConstant.USER_FOLLOW_KEY + id,
                RedisKeyConstant.USER_FOLLOW_EXPIRE_TIME, TimeUnit.MILLISECONDS);
        return ids;
    }

    /**
     * 获取粉丝列表
     * @param idPageDto
     * @return
     */
    @Override
    public RPage<UserVo> listFans(IdPageDto idPageDto) {
        IPage<Long> ids = followMapper.selectFanIdsByUserId(
                new Page<>(idPageDto.getCurrentPage(), idPageDto.getPageSize()), idPageDto.getId());
        if (ids.getRecords().isEmpty()){
            return new RPage<>(0L, null);
        }
        List<UserVo> userVoList = userService.selectUserVoListByIds(ids.getRecords());
        return new RPage<>(ids.getTotal(), userVoList);
    }


    /**ids
     * 获取关注列表
     * @param idPageDto
     * @return
     */
    @Override
    public RPage<UserVo> listFollow(IdPageDto idPageDto) {
        List<Long> followList = getFollowList(idPageDto.getId());
        //分页查询
        return pageQueryFollows(idPageDto, followList);
    }

    /**
     * 获取共同关注列表
     * @param idPageDto
     * @return
     */
    @Override
    public RPage<UserVo> listCommonFollow(IdPageDto idPageDto) {
        List<Long> followList1 = getFollowList(idPageDto.getId());
        if( followList1.isEmpty()){
            return new RPage<>(0L, null);
        }
        List<Long> followList2 = getFollowList(CurrentHolder.getCurrentUser().getId());
        if( followList2.isEmpty()){
            return new RPage<>(0L, null);
        }
        //求交集
        List<Long> commonFollowIds = followList1.stream().filter(followList2::contains).toList();
        //分页查询
        return pageQueryFollows(idPageDto, commonFollowIds);
    }

    //分页查询
    private RPage<UserVo> pageQueryFollows(IdPageDto idPageDto, List<Long> followIds) {
        if( followIds.isEmpty()){
            return new RPage<>(0L, null);
        }
        //截取列表
        List<Long> ids = followIds.subList(
                idPageDto.getCurrentPage()* idPageDto.getPageSize() , Math.min(followIds.size(), idPageDto.getPageSize()));
        List<UserVo> userVoList = userService.selectUserVoListByIds(ids);
        return new RPage<>((long) ids.size(), userVoList);
    }


}
