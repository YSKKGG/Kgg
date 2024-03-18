package com.kgg.kkchat.common.user.domain.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description:
 * Author: Kgg
 * Date: 2024/3/10
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendResp {
    @ApiModelProperty("好友uid")
    private Long uid;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("头像")
    private String avatar;

    @ApiModelProperty("在线状态 1在线 2离线")
    private Integer activeStatus;
}
