package com.zfh.controller;


import com.zfh.dto.IdPageDto;
import com.zfh.result.R;
import com.zfh.service.IFollowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 *
 * 用户关注controller
 * @author author
 * @since 2025-09-19
 */
@RestController
@RequestMapping("/client/follow")
@Slf4j
public class FollowController {
    @Autowired
    private IFollowService followService;

    /**
     * 关注或取消关注
     * @param id
     * @param isFollow
     * @return
     */
    @PutMapping("/{id}/{isFollow}")
    public R follow(@PathVariable Long id, @PathVariable Boolean isFollow) {
        log.info("关注或取消关注：{}", id);
        return R.OK(followService.follow(id, isFollow));
    }


    /**
     * 查询是否关注
     * @param id
     * @return
     */
    @GetMapping("/or/not/{id}")
    public R isFollow(@PathVariable Long id) {
        log.info("查询是否关注：{}", id);
        return R.OK(followService.isFollow(id));
    }


    /**
     * 查询粉丝列表
     * @return
     */
    @GetMapping("/fansList")
    public R fans(IdPageDto idPageDto) {
        log.info("查询粉丝列表");
        return R.OK(followService.listFans(idPageDto));
    }

    /**
     * 查询关注列表
     * @param idPageDto
     * @return
     */
    @GetMapping("/followList")
    public R followList(IdPageDto idPageDto) {
        log.info("查询关注列表");
        return R.OK(followService.listFollow(idPageDto));
    }

    /**
     * 查询共同关注列表
     * @param idPageDto
     * @return
     */
    @GetMapping("/common")
    public R common(IdPageDto idPageDto) {
        log.info("查询共同关注列表：{}",idPageDto);
        return R.OK(followService.listCommonFollow(idPageDto));
    }

}
