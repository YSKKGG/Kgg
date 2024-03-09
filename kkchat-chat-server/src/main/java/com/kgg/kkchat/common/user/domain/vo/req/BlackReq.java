package com.kgg.kkchat.common.user.domain.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Description:
 * Author: Kgg
 * Date: 2024/3/7
 */
@Data
public class BlackReq {
    @ApiModelProperty("被拉黑用户的uid")
    @NotNull
    private Long uid;

}
