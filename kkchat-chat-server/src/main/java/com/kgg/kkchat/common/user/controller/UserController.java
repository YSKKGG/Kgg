package com.kgg.kkchat.common.user.controller;


import com.kgg.kkchat.common.common.domain.vo.resp.ApiResult;
import com.kgg.kkchat.common.common.utils.AssertUtil;
import com.kgg.kkchat.common.common.utils.RequestHolder;
import com.kgg.kkchat.common.user.domain.dto.ItemInfoDTO;
import com.kgg.kkchat.common.user.domain.dto.SummeryInfoDTO;
import com.kgg.kkchat.common.user.domain.enums.RoleEnum;
import com.kgg.kkchat.common.user.domain.vo.req.*;
import com.kgg.kkchat.common.user.domain.vo.resp.BadgeResp;
import com.kgg.kkchat.common.user.domain.vo.resp.UserInfoResp;
import com.kgg.kkchat.common.user.service.IRoleService;
import com.kgg.kkchat.common.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author kgg
 * @since 2023-12-20
 */
@RestController
@RequestMapping("/capi/user")
@Api(tags="用户相关接口")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private IRoleService roleService;

    @GetMapping("/uerInfo")
    @ApiOperation("获取用户信息")
    public ApiResult<UserInfoResp> getUserInfo(){
        return ApiResult.success(userService.getUserInfo(RequestHolder.get().getUid()));
    }

    @PutMapping("/name")
    @ApiOperation("修改用户名")
    public ApiResult<Void> modifyName(@Valid @RequestBody ModifyNameReq modifyNameReq){
        userService.modifyName(RequestHolder.get().getUid(),modifyNameReq.getName());
        return ApiResult.success();
    }

    @PostMapping("/public/summary/userInfo/batch")
    @ApiOperation("用户聚合信息-返回的代表需要刷新的")
    public ApiResult<List<SummeryInfoDTO>> getSummeryUserInfo(@Valid @RequestBody SummeryInfoReq req) {
        return ApiResult.success(userService.getSummeryUserInfo(req));
    }

    @PostMapping("/public/badges/batch")
    @ApiOperation("徽章聚合信息-返回的代表需要刷新的")
    public ApiResult<List<ItemInfoDTO>> getItemInfo(@Valid @RequestBody ItemInfoReq req) {
        return ApiResult.success(userService.getItemInfo(req));
    }

    @GetMapping("/badges")
    @ApiOperation("可选徽章预览")
    public ApiResult<List<BadgeResp>> badges( ){
        return ApiResult.success(userService.badges(RequestHolder.get().getUid()));
    }

    @PutMapping("/badge")
    @ApiOperation("佩戴徽章")
    public ApiResult<List<BadgeResp>> wearingBadge(@Valid @RequestBody WearingBadgeReq req){
        userService.wearingBadge(RequestHolder.get().getUid(),req.getItemId());
        return ApiResult.success();
    }

    @PutMapping("/black")
    @ApiOperation("拉黑用户")
    public ApiResult<List<BadgeResp>> black(@Valid @RequestBody BlackReq req){
        Long uid = RequestHolder.get().getUid();
        boolean b = roleService.hasPower(uid, RoleEnum.ADMIN);
        AssertUtil.isTrue(b,"没权限");
        userService.black(req);
        return ApiResult.success();
    }
}

