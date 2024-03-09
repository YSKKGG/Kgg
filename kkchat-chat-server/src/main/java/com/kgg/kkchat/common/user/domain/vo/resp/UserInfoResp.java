package com.kgg.kkchat.common.user.domain.vo.resp;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Description:
 * Author: Kgg
 * Date: 2024/3/6
 */
@Data

public class UserInfoResp {
    @ApiModelProperty(value="uid")
    private Long id;
    @ApiModelProperty(value="用户名称")
    private String name;
    @ApiModelProperty(value="头像")
    private String avatar;
    @ApiModelProperty(value="性别 1为男性,2为女性")
    private Integer sex;
    @ApiModelProperty(value="改名次数")
    private Integer modifyNameChance;

}
