package com.zfh.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zfh.constant.ExceptionConstant;
import com.zfh.constant.RedisKeyConstant;
import com.zfh.constant.UserConstant;
import com.zfh.dto.UserInfoDto;
import com.zfh.dto.UserPasswordDto;
import com.zfh.dto.UserRegisterDto;
import com.zfh.entity.Blog;
import com.zfh.entity.BlogComments;
import com.zfh.entity.User;
import com.zfh.entity.UserInfo;
import com.zfh.exception.UserLoginException;
import com.zfh.mapper.UserMapper;
import com.zfh.service.IBlogCommentsService;
import com.zfh.service.IBlogService;
import com.zfh.service.IUserInfoService;
import com.zfh.service.IUserService;
import com.zfh.utils.CurrentHolder;
import com.zfh.vo.UserSelfVo;
import com.zfh.vo.UserVo;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.zfh.constant.UserConstant.USER_STATUS_ENABLE;

/**
 * 用户服务类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private IUserInfoService userInfoService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;
    @Autowired
    private IBlogService blogService;
    @Autowired
    private IBlogCommentsService blogCommentsService;

    /**
     * 根据用户名查询用户(spring-security)
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //查询用户被锁
        String s = stringRedisTemplate.opsForValue().get(RedisKeyConstant.USER_LOCK_KEY + username);
        int i=0;
        if (s != null) {
            i = Integer.parseInt(s);
        }
        if(i>=5){
            throw new UserLoginException(ExceptionConstant.USER_LOCK);
        }

        //根据用户名查询用户
        User user = this.getOne(new QueryWrapper<User>().eq("username", username));
        if (user == null) {
            //redis保存用户信息
            stringRedisTemplate.opsForValue().increment(RedisKeyConstant.USER_LOCK_KEY + username);
            stringRedisTemplate.expire(RedisKeyConstant.USER_LOCK_KEY + username,
                    RedisKeyConstant.USER_LOCK_EXPIRE_TIME, TimeUnit.MILLISECONDS);
            throw new UsernameNotFoundException(ExceptionConstant.USER_NOT_EXIST);
        }

        return user;
    }

    /**
     *  注册
     * @param userRegisterDto 注册信息
     * @return
     */
    @Transactional
    @Override
    public int register(UserRegisterDto userRegisterDto) {
        //查询用户名是否已存在
        User user = this.getOne(new QueryWrapper<User>().eq("username", userRegisterDto.getUsername()));
        if (user != null) {
            throw new UserLoginException(ExceptionConstant.USERNAME_EXIST);
        }
        user=new User();

        //把密码加密
        String encode = passwordEncoder.encode(userRegisterDto.getPassword());
        BeanUtils.copyProperties(userRegisterDto, user);
        user.setPassword(encode);

        Date now = new Date();
        //补充剩余信息
        user.setUserType(UserConstant.USER_TYPE_NORMAL);
        //security框架信息
        user.setAccountNoExpired(USER_STATUS_ENABLE);
        user.setCredentialsNoExpired(USER_STATUS_ENABLE);
        user.setAccountNoLocked(USER_STATUS_ENABLE);
        user.setAccountEnabled(USER_STATUS_ENABLE);
        user.setCreateTime(now);
        user.setUpdateTime(now);
        boolean res1 = this.save(user);

        //创建详细信息
        return userInfoService.registerInfo(user);
    }

    /**
     * 获取当前用户信息
     * @return
     */
    @Override
    public UserSelfVo getCurrentUserInfo() {
        //获取当前用户id
        Long id = CurrentHolder.getCurrentUser().getId();
        if(id == null){
            throw new UserLoginException(ExceptionConstant.USER_NOT_LOGIN);
        }
        return userMapper.selectUserSelfInfoById(id);
    }

    /**
     * 修改当前用户信息
     * @param userInfoDto
     * @return
     */
    @Override
    public int updateCurrent(UserInfoDto userInfoDto) {
        if(userInfoDto.getId() == null){
            throw new UserLoginException(ExceptionConstant.USER_NOT_LOGIN);
        }
        //先判断是否是当前用户
        User user1 = CurrentHolder.getCurrentUser();
        if(!user1.getId().equals(userInfoDto.getId())){
            throw new UserLoginException(ExceptionConstant.USER_NOT_LOGIN);
        }
        //封装user和userInfo
        User user = new User();
        BeanUtils.copyProperties(userInfoDto, user);
        user.setUpdateTime(new Date());
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(userInfoDto, userInfo);
        userInfo.setUpdateTime(new Date());
        userInfo.setUserId(user.getId());

        //更新redis用户信息
        stringRedisTemplate.opsForHash().put(RedisKeyConstant.USER_TOKEN_KEY + user.getId(), "username", user.getUsername());
        stringRedisTemplate.opsForHash().put(RedisKeyConstant.USER_TOKEN_KEY + user.getId(), "nickName", user.getNickName());
        stringRedisTemplate.opsForHash().put(RedisKeyConstant.USER_TOKEN_KEY + user.getId(), "icon", user.getIcon());


        //异步更新用户信息,关联的评论,文章
        UserServiceImpl userService  = (UserServiceImpl)AopContext.currentProxy();
        userService.updateUser(user, userInfo);

         return 1;
    }

    @Transactional
    @Async("threadPoolExecutor")
    public void updateUser(User user, UserInfo userInfo) {
       threadPoolExecutor.execute(() -> {
           //更新user和userInfo
           updateById(user);
           userInfoService.updateById(userInfo) ;
           //更新关联博客,评论...TODO 待完善
           blogService.update(new LambdaUpdateWrapper<Blog>()
                   .set(Blog::getUserNickName, user.getNickName())
                    .set(Blog::getUserIcon, user.getIcon())
                   .eq(Blog::getUserId, user.getId()));

           blogCommentsService.update(new LambdaUpdateWrapper<BlogComments>()
                   .set(BlogComments::getUserNickName, user.getNickName())
                   .set(BlogComments::getUserIcon, user.getIcon())
                   .eq(BlogComments::getUserId, user.getId()));
       });

    }

    /**
     * 修改当前用户密码
     *
     * @param userPasswordDto
     * @return
     */
    @Override
    public boolean updateCurrentPassword(UserPasswordDto userPasswordDto) {
        //获取当前线程用户
        User user = CurrentHolder.getCurrentUser();
        String password = userMapper.getPasswordById(user.getId());

        //比对旧密码
        if(!passwordEncoder.matches(userPasswordDto.getPassword(), password)){
            throw new UserLoginException(ExceptionConstant.USER_PASSWORD_ERROR);
        }

        // Lambda方式（类型安全，推荐）
        return update(new LambdaUpdateWrapper<User>()
                .set(User::getPassword, passwordEncoder.encode(userPasswordDto.getNewPassword()))
                .eq(User::getId, user.getId()));
    }

    /**
     * 获取用户信息
     * @param id
     * @return
     */
    @Override
    public UserVo getInfoById(Long id) {

        UserVo userVo = userMapper.selectUserInfoById(id);
        return userVo;
    }

    /**
     * 批量获取用户信息
     * @param ids
     * @return
     */
    @Override
    public List<UserVo> selectUserVoListByIds(List<Long> ids) {
        return userMapper.selectUserVoListByIds(ids);
    }

    /**
     * 根据用户名查询用户信息
     * @param username
     * @return
     */
    @Override
    public UserVo getInfoByUsername(String username) {
        UserVo userVo = userMapper.selectUserInfoByUsername(username);
        return userVo;
    }

    /**
     * 根据用户名查询用户id
     * @param username
     * @return
     */
    @Override
    public Long getIdByUsername(String username) {
        return userMapper.selectIdByUsername(username);
    }

    /**
     * 成为商家
     *
     * @param userId
     * @return
     */
    @Override
    public boolean beBussiness(Long userId) {
        return update(new LambdaUpdateWrapper<User>()
                .set(User::getUserType, UserConstant.USER_TYPE_BUSINESS)
                .eq(User::getId, userId));
    }
}
