package com.zfh.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zfh.dto.IdPageDto;
import com.zfh.entity.Follow;
import com.zfh.result.RPage;
import com.zfh.vo.UserVo;

/**
 * <p>
 * 用户关注表 服务类
 * </p>
 *
 * @author author
 * @since 2025-09-19
 */
public interface IFollowService extends IService<Follow> {

    /**
     * 关注或取关
     * @param id
     * @param isFollow
     * @return
     */
    int follow(Long id, Boolean isFollow);

    /**
     * 判断是否关注
     * @param id
     * @return
     */
    Boolean isFollow(Long id);

    /**
     * 粉丝列表
     * @param idPageDto
     * @return
     */
    RPage<UserVo> listFans(IdPageDto idPageDto);
}
