package com.kgg.kkchat.common.user.domain.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * Description:
 * Author: Kgg
 * Date: 2024/3/7
 */
@Data
public class ModifyNameReq {
    @ApiModelProperty("用户名")
    @NotBlank
    @Length(max = 6,message = "用户名太长了")
    private String name;

}
