package com.kgg.kkchat.common.websocket.domain.vo.resp;

import lombok.Data;

/**
 * Description:
 * Author: Kgg
 * Date:2023-12-20
 */
@Data
public class WSBaseResp<T> {
    /**
     * @see com.kgg.kkchat.common.websocket.domain.enums.WSRespTypeEnum
     */
    private Integer type;
    private T data;
}
