package com.kgg.kkchat.common.user.domain.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Description:
 * Author: Kgg
 * Date: 2024/3/7
 */
@Data
public class WearingBadgeReq {
    @ApiModelProperty("徽章id")
    @NotNull
    private Long itemId;

}
